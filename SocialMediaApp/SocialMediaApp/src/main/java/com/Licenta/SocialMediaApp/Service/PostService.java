package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    int getPostsNrOfUser(Long userId);
    Post createPost(String jwt, MultipartFile file, String text) throws IOException;
    void deletePost(Long postId, String jwt) throws Exception;
    List<Post> getAllPostsByUser(Long userId);
    long getLikesCountForPost(Long postId);
    List<UserResponse> getUsersWhoLikedPost(Long postId);
    byte[] getPostMedia(Long postId) throws Exception;
    String getMediaKey(Long postId) throws Exception;
    List<Post> getPostsByFriends(String jwt);
    Post updatePostContent(Long postId, String content, MultipartFile file, String jwt) throws Exception;
    List<Post> getPostsOrderedByReportCount();
}
