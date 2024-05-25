package com.Licenta.SocialMediaApp.Model.BodyResponse;

import com.Licenta.SocialMediaApp.Model.Content;
import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Model.Enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MessageResponse {
    private int id;

    private Conversation conversation;

    private UserResponse sender;

    private Content content;

    private LocalDateTime timestamp;

    private MessageType messageType;
}
