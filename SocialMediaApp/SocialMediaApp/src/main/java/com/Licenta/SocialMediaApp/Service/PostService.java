package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    int getPostsNrOfUser(int userId);
    Post createPost(String jwt, MultipartFile file, String text) throws IOException;
    void deletePost(int postId, String jwt) throws Exception;
    List<Post> getAllPostsByUser(int userId);
    long getLikesCountForPost(int postId);
    List<UserResponse> getUsersWhoLikedPost(int postId);
    byte[] getPostMedia(int postId) throws Exception;
    String getMediaKey(int postId) throws Exception;
    List<Post> getPostsByFriends(String jwt);
    Post updatePostContent(int postId, String content, MultipartFile file, String jwt) throws Exception;
    List<Post> getPostsOrderedByReportCount();
}
