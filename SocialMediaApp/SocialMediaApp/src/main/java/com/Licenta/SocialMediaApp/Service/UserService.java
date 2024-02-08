package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User findUserByJwt(String jwt);
    void updateUsername(int userId, String newUsername) throws Exception;
    boolean existsByUsername(String username);
    List<User> findByUsernameContainingIgnoreCase(String username);
    void changePassword(String oldPassword, String newPassword, String jwt);
    void registerUser(User user, MultipartFile profileImage) throws Exception;
}
