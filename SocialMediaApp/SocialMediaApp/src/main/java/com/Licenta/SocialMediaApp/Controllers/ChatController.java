package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
/*    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/publicChatRoom")
    public Message sendMessage(@Payload Message chatMessage) {
        return chatMessage;
    }*/

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message sendMessage(@Payload Message chatMessage) {// Set the current timestamp
        return chatMessage;
    }
}
