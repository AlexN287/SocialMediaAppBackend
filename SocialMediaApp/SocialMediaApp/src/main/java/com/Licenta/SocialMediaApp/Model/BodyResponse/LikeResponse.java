package com.Licenta.SocialMediaApp.Model.BodyResponse;

import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class LikeResponse {
    private int id;

    private UserResponse user;

    private PostResponse post;
}
