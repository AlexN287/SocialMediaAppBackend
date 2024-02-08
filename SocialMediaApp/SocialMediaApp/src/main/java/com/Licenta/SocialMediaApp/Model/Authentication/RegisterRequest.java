package com.Licenta.SocialMediaApp.Model.Authentication;

import com.Licenta.SocialMediaApp.Model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private User user;
    private MultipartFile profileImage;
}
