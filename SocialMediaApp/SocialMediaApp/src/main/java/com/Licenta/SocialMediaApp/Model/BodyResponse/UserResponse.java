package com.Licenta.SocialMediaApp.Model.BodyResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String profileImagePath;

    public UserResponse(String username, String email, String profileImagePath) {
        this.username = username;
        this.email = email;
        this.profileImagePath = profileImagePath;
    }
}
