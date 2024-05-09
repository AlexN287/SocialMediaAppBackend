package com.Licenta.SocialMediaApp.Model.BodyResponse;

import com.Licenta.SocialMediaApp.Model.Content;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private int id;
    private Content content;
    private LocalDateTime timestamp;
    private UserResponse user;

}

