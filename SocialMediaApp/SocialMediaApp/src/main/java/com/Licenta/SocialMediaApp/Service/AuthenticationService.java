package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface AuthenticationService {
    Authentication authenticate(String username, String password);
    void registerUser(User user, MultipartFile profileImage) throws Exception;
}

