package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;

public interface UserService {
    User findUserByJwt(String jwt);
    String updateUsername(String newUsername, String jwt) throws Exception;
    boolean existsByUsername(String username);
    List<User> findByUsernameContainingIgnoreCase(String username);
    List<User> searchByUsernameExcludingLoggedInUser(String username, String jwt);
    void changePassword(String oldPassword, String newPassword, String jwt);
    User findById(Long userId) throws EntityNotFoundException;
    void uploadUserProfileImage(Long userId, MultipartFile file) throws Exception;
    byte[] getUserProfileImage(Long userId, String jwt) throws Exception;
    List<UserResponse> getConnectedFriends(String jwt);
}
