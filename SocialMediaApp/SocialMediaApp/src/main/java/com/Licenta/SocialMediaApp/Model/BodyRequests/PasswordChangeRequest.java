package com.Licenta.SocialMediaApp.Model.BodyRequests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeRequest {
    private String newPassword;
    private String oldPassword;
}
