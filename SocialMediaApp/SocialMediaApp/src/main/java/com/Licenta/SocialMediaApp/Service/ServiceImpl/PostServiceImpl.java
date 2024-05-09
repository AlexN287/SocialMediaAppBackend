package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.Content;
import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.ContentRepository;
import com.Licenta.SocialMediaApp.Repository.LikeRepository;
import com.Licenta.SocialMediaApp.Repository.PostRepository;
import com.Licenta.SocialMediaApp.Service.PostService;
import com.Licenta.SocialMediaApp.Service.UserService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final ContentRepository contentRepository;
    private final S3Service s3Service;

    public PostServiceImpl(PostRepository postRepository, LikeRepository likeRepository, UserService userService,
                           ContentRepository contentRepository, S3Service s3Service){
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.contentRepository = contentRepository;
        this.s3Service = s3Service;
    }
    @Override
    public int getPostsNrOfUser(int userId) {
        return postRepository.countByUserId(userId);
    }

    @Transactional
    @Override
    public Post createPost(String jwt, MultipartFile file, String text) throws IOException {
        User loggedUser = userService.findUserByJwt(jwt);

        if (loggedUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Create and save the post
        Post post = new Post();
        post.setUser(loggedUser);
        post.setCreatedAt(LocalDateTime.now());

        // Create and save the content
        Content content = new Content();
        content.setTextContent(text);

        if (file != null && !file.isEmpty()) {
            String filePath = s3Service.generatePostKey(post.getId(), loggedUser.getId(), file);
            content.setFilePath(filePath);
            s3Service.putObject(filePath, file.getBytes());
        }

        content = contentRepository.save(content);
        post.setContent(content);

        return postRepository.save(post);
    }


    @Override
    public void deletePost(int postId, String jwt) throws Exception {
        User loggedUser = userService.findUserByJwt(jwt);
        if (loggedUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (post.getUser().getId()!=(loggedUser.getId())) {
            throw new IllegalAccessException("Unauthorized to delete this post");
        }

        postRepository.delete(post);
    }

    @Override
    public List<Post> getAllPostsByUser(int userId) {
        return postRepository.findByUser_Id(userId);
    }

    @Override
    public long getLikesCountForPost(int postId) {
        return likeRepository.countByPostId(postId);
    }

    @Override
    public List<UserResponse> getUsersWhoLikedPost(int postId) {
        List<User> users = likeRepository.findUsersByPostId(postId);
        return users.stream()
                .map(Utils::convertToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] getPostMedia(int postId) throws Exception {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));

        String mediaKey = post.getContent().getFilePath();
        if (mediaKey == null || mediaKey.isEmpty()) {
            throw new IllegalArgumentException("No media available for this post");
        }

        try {
            return s3Service.getObject(mediaKey);  // This should call your S3 service or storage service
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve media", e);
        }
    }

    public String getMediaKey(int postId) throws Exception{
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));

        return post.getContent().getFilePath();
    }

    @Override
    public List<Post> getPostsByFriends(String jwt) {
        User loggedUser = userService.findUserByJwt(jwt);

        return postRepository.findAllPostsByFriends(loggedUser.getId());
    }

}
