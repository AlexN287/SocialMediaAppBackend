package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {
    Message sendMessage(Message message);
    Page<Message> getMessagesByConversationId(Long conversationId, Pageable pageable);

}
