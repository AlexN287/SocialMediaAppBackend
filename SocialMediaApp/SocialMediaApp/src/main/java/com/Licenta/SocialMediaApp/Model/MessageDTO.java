package com.Licenta.SocialMediaApp.Model;

import com.Licenta.SocialMediaApp.Model.Enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private Integer senderId;
    private Integer conversationId;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp;
}
