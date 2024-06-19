package com.Licenta.SocialMediaApp.Model;

import com.Licenta.SocialMediaApp.Model.Enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private Long senderId;
    private Long conversationId;
    private String content;
    private MessageType type;
    private String filePath;
    private LocalDateTime timestamp;
}
