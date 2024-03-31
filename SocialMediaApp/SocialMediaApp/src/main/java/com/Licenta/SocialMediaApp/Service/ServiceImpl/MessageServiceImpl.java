package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Model.Message;
import com.Licenta.SocialMediaApp.Repository.ContentRepository;
import com.Licenta.SocialMediaApp.Repository.ConversationRepository;
import com.Licenta.SocialMediaApp.Repository.MessageRepository;
import com.Licenta.SocialMediaApp.Service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ContentRepository contentRepository;

    public MessageServiceImpl(MessageRepository messageRepository, ConversationRepository conversationRepository, ContentRepository contentRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.contentRepository = contentRepository;
    }

    @Override
    @Transactional
    public Message sendMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        Message newMessage = messageRepository.save(message);

        Conversation conversation = conversationRepository.findById(newMessage.getConversation().getId())
                .orElseThrow(() -> new RuntimeException("Conversation not found with ID: " + newMessage.getConversation().getId()));

        conversation.setLastUpdated(message.getTimestamp());

        if(message.getContent().getTextContent()!= null)
        {
            conversation.setLastMessage(message.getContent().getTextContent());
        }

        conversationRepository.save(conversation);

        contentRepository.save(message.getContent());
        messageRepository.save(message);

        return newMessage;
    }
}
