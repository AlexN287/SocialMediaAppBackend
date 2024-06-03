package com.Licenta.SocialMediaApp.Model.BodyResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWithRoles {
    private Long id;
    private String username;
    private Set<String> roles;
    private String email;
    private String profileImagePath;
}
