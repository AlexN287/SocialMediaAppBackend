package com.Licenta.SocialMediaApp.Model.BodyResponse;

import com.Licenta.SocialMediaApp.Model.Content;
import com.Licenta.SocialMediaApp.Model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private int id;
    private UserResponse user;
    private Content content;
    private LocalDateTime createdAt;
}
