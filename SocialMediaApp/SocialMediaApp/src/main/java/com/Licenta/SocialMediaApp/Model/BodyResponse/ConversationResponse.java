package com.Licenta.SocialMediaApp.Model.BodyResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private String conversationImagePath;
    private boolean isGroup;
    private String lastMessage;
    private LocalDateTime lastUpdated;
}
