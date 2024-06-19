package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MessageService {
    Message sendMessage(Message message) ;
    Page<Message> getMessagesByConversationId(Long conversationId, Pageable pageable);
    Optional<Message> getLastMessageByConversationId(Long conversationId);
    String sendFile(MultipartFile file, String textContent, Long conversationId, Long senderId) throws IOException;

    byte[] getMessageMedia(String filePath) throws Exception;
    String getMediaKey(Long messageId) throws Exception;
    Optional<Message> getLastMessageByConversationIdAndSenderId(Long conversationId, Long senderId);
}
