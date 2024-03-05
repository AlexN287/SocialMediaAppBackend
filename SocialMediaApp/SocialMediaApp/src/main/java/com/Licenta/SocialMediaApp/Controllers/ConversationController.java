package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.BodyRequests.GroupRequest;
import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversation")
public class ConversationController {
    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("/all")
    public List<Conversation> getUserConversations(@RequestHeader("Authorization")String jwt) {
         return conversationService.getUsersConversation(jwt);
    }

    @PostMapping("/private/create")
    public ResponseEntity<?> createPrivateConversation(
            @RequestParam int userId,
            @RequestHeader("Authorization") String jwt) {
        try {
            conversationService.createPrivateConversation(userId, jwt);
            return ResponseEntity.ok().body("Private conversation created successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create private conversation: " + e.getMessage());
        }
    }
    @PostMapping("/group/create")
    public ResponseEntity<String> createGroupConversation(@RequestBody GroupRequest groupRequest)
    {
        try {
            conversationService.createGroupConversation(groupRequest);
            return ResponseEntity.ok().body("Group conversation created successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create group conversation: " + e.getMessage());
        }
    }
    @PostMapping("/group/{conversationId}/members/add")
    public ResponseEntity<?> addGroupMember(
            @PathVariable int conversationId,
            @RequestParam int userId) {
        try {
            conversationService.addGroupMember(conversationId, userId);
            return ResponseEntity.ok().body("User added to the group successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding user to the group: " + e.getMessage());
        }
    }
    @DeleteMapping("/group/{conversationId}/members/remove")
    public ResponseEntity<?> removeGroupMember(
            @PathVariable int conversationId,
            @RequestParam int userId) {
        try {
            conversationService.removeGroupMember(conversationId, userId);
            return ResponseEntity.ok().body("User removed from the group successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error removing user from the group: " + e.getMessage());
        }
    }
    @DeleteMapping("/group/{conversationId}/leave")
    public ResponseEntity<?> leaveGroup(@PathVariable int conversationId, @RequestHeader("Authorization") String jwt) {
        try {
            conversationService.leaveGroup(conversationId, jwt);
            return ResponseEntity.ok().body("Left group successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error leaving the group: " + e.getMessage());
        }
    }
}
