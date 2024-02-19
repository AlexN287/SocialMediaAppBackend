package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conversation")
public class ConversationController {
    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping()
    public List<Conversation> getUserConversations(@RequestHeader("Authorization")String jwt) {
         return conversationService.getUsersConversation(jwt);
    }
}
