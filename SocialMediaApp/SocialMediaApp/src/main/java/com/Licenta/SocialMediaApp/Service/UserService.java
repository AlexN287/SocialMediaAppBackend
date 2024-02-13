package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.User;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;

public interface UserService {
    User findUserByJwt(String jwt);
    void updateUsername(String newUsername, String jwt) throws Exception;
    boolean existsByUsername(String username);
    List<User> findByUsernameContainingIgnoreCase(String username);
    void changePassword(String oldPassword, String newPassword, String jwt);
    void registerUser(User user, MultipartFile profileImage) throws Exception;
    User findById(int userId) throws Exception;
}
