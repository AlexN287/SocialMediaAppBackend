package com.Licenta.SocialMediaApp.Model.BodyResponse;

import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReportResponse {
    private Long id;

    private UserResponse user;
    private String reason;

    private LocalDateTime reportTime;
}
