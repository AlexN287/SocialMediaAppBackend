package com.Licenta.SocialMediaApp.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String message;
    private String fromUserId;
    private String fromUsername;
    //private String type; // Optional, for differentiating notification types
    private LocalDateTime timestamp;
}
