package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.Message;
import com.Licenta.SocialMediaApp.Model.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Example method to handle sending a message within a private conversation
    @MessageMapping("/chat/{conversationId}")
    public void sendMessage(@DestinationVariable String conversationId, @Payload MessageDTO chatMessage, Principal principal) {
        // Logic to ensure the user is part of the conversation goes here

        // Sends message to a specific conversation topic, which subscribers of this conversation will listen to
        messagingTemplate.convertAndSend(String.format("/topic/conversations/%s", conversationId), chatMessage);
    }

    /*@MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message sendMessage(@Payload Message chatMessage) {
        // Process the message if needed
        return chatMessage;
    }*/
}
