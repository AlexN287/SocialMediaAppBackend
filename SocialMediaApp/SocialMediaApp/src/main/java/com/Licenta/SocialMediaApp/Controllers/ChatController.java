package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Model.*;
import com.Licenta.SocialMediaApp.Model.Enums.MessageType;
import com.Licenta.SocialMediaApp.Service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    public ChatController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;

    }

    // Example method to handle sending a message within a private conversation
    @Transactional
    @MessageMapping("/chat/{conversationId}")
    public void sendMessage(@DestinationVariable String conversationId, @Payload MessageDTO chatMessage, Principal principal) throws IOException {
        // Logic to ensure the user is part of the conversation goes here
        // Logic to ensure the user is part of the conversation goes here

        if(chatMessage.getType()!= MessageType.FILE){
            Conversation conversation = new Conversation();
            conversation.setId(chatMessage.getConversationId());

            User user = new User();
            user.setId(chatMessage.getSenderId());

            Content content = new Content();
            content.setTextContent(chatMessage.getContent());

            Message newMessage = new Message(conversation, user, content, null, chatMessage.getType());
            messageService.sendMessage(newMessage);
        }


        // Sends message to a specific conversation topic, which subscribers of this conversation will listen to
        messagingTemplate.convertAndSend(String.format("/topic/conversations/%s", conversationId), chatMessage);
    }
}
