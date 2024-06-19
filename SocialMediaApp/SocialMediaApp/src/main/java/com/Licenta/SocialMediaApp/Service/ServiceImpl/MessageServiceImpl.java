package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Model.Content;
import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Model.Message;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.ContentRepository;
import com.Licenta.SocialMediaApp.Repository.ConversationRepository;
import com.Licenta.SocialMediaApp.Repository.MessageRepository;
import com.Licenta.SocialMediaApp.Service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ContentRepository contentRepository;
    private final S3Service s3Service;
    public MessageServiceImpl(MessageRepository messageRepository, ConversationRepository conversationRepository, ContentRepository contentRepository, S3Service s3Service) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.contentRepository = contentRepository;
        this.s3Service = s3Service;
    }

    @Override
    @Transactional
    public Message sendMessage(Message message){
        message.setTimestamp(LocalDateTime.now());
        Message newMessage = messageRepository.save(message);

        Conversation conversation = conversationRepository.findById(newMessage.getConversation().getId())
                .orElseThrow(() -> new RuntimeException("Conversation not found with ID: " + newMessage.getConversation().getId()));

        //conversationRepository.save(conversation)
        //
        // ;

        contentRepository.save(message.getContent());
        messageRepository.save(message);

        return newMessage;
    }
    public Page<Message> getMessagesByConversationId(Long conversationId, Pageable pageable) {
        return messageRepository.findByConversationId(conversationId, pageable);
    }

    @Override
    public Optional<Message> getLastMessageByConversationId(Long conversationId) {
        return messageRepository.findLatestMessageByConversationId(conversationId);
    }
    @Transactional
    @Override
    public String sendFile(MultipartFile file, String textContent, Long conversationId, Long senderId) throws IOException {
        //Optional<Message> optionalMessage = messageRepository.findLastMessageByConversationIdAndSenderId(conversationId, senderId);

       /* if (!optionalMessage.isPresent()) {
            throw new RuntimeException("No previous message found for this conversation and sender");
        }*/

        // Update the existing message
        //Message message = optionalMessage.get();\
        Message message = new Message();
        message.setTimestamp(LocalDateTime.now());

        User sender = new User();
        sender.setId(senderId);
        message.setSender(sender);

        Conversation conversation = new Conversation();
        conversation.setId(conversationId);
        message.setConversation(conversation);

        Content content = new Content();
        content.setTextContent(textContent);

        message.setContent(content);
        Content createdContent = contentRepository.save(content);

        Message createdMessage = messageRepository.save(message);

        // Generate the S3 key and upload the file
        String key = s3Service.generateConversationFileKey(conversationId, createdMessage.getId(), file);
        s3Service.putObject(key, file.getBytes());

        // Update the message content with the file path
        //Content content = message.getContent();
        createdContent.setFilePath(key);
        contentRepository.save(createdContent);
        return key;
    }

    @Override
    public byte[] getMessageMedia(String filePath) throws Exception {
        /*Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found with ID: " + messageId));*/

        //String mediaKey = message.getContent().getFilePath();
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("No media available for this message");
        }

        try {
            return s3Service.getObject(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve media", e);
        }
    }
    @Override
    public String getMediaKey(Long messageId) throws Exception {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found with ID: " + messageId));
        return message.getContent().getFilePath();
    }
    @Override
    public Optional<Message> getLastMessageByConversationIdAndSenderId(Long conversationId, Long senderId) {
        return messageRepository.findLastMessageByConversationIdAndSenderId(conversationId, senderId);
    }
}
