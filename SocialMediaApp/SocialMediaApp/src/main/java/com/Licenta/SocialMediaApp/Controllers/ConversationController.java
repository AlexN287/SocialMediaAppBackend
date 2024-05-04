package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Exceptions.ConversationAlreadyExistsException;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Model.Message;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.ConversationService;
import com.Licenta.SocialMediaApp.Service.MessageService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/conversation")
public class ConversationController {
    private final ConversationService conversationService;
    private final MessageService messageService;

    public ConversationController(ConversationService conversationService, MessageService messageService) {
        this.conversationService = conversationService;
        this.messageService = messageService;
    }

    @GetMapping("/all")
    public List<Conversation> getUserConversations(@RequestHeader("Authorization")String jwt) {
         return conversationService.getUsersConversation(jwt);
    }

    @PostMapping("/private/create")
    public ResponseEntity<?> createPrivateConversationControllerMethod(
            @RequestParam int userId,
            @RequestHeader("Authorization") String jwt) {
        try {
            conversationService.createPrivateConversation(userId, jwt);
            return ResponseEntity.ok().body("Private conversation created successfully.");
        } catch (ConversationAlreadyExistsException e) {
            // Return a bad request or conflict response
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            // Generic error handling
            return ResponseEntity.badRequest().body("Failed to create private conversation: " + e.getMessage());
        }
    }

    @PostMapping("/group/create")
    public ResponseEntity<String> createGroupConversation(
            @RequestParam String name,
            @RequestParam List<Integer> members,
            @RequestParam MultipartFile groupImage,
            @RequestHeader("Authorization")String jwt) {
        try {
            conversationService.createGroupConversation(name, groupImage, members ,jwt);
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
    @DeleteMapping("/group/{conversationId}/members/remove/{userId}")
    public ResponseEntity<?> removeGroupMember(
            @PathVariable int conversationId,
            @PathVariable int userId) {
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
    @GetMapping("/{conversationId}/image")
    public ResponseEntity<?> loadConversationImage(@PathVariable int conversationId, @RequestHeader("Authorization") String jwt)
    {
        try {
            byte[] image = conversationService.loadConversationImage(conversationId, jwt);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Set appropriate content type based on the image
                    .body(new ByteArrayResource(image));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/{conversationId}/messages")
    public List<Message> getMessagesByConversationId(@PathVariable int conversationId) {
        return messageService.getMessagesByConversationId(conversationId);
    }
    @GetMapping("/{conversationId}/members")
    public ResponseEntity<List<UserResponse>> getConversationMembers(@PathVariable int conversationId) {
        try {
            List<User> members = conversationService.getMembersByConversationId(conversationId);
            List<UserResponse> memberDTOs = members.stream()
                    .map(Utils::convertToUserResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(memberDTOs);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{conversationId}/friends-not-in-conversation")
    public ResponseEntity<List<UserResponse>> getFriendsNotInConversation(@PathVariable int conversationId,
                                                                          @RequestHeader("Authorization") String token) {

        try {
            List<UserResponse> friendsNotInConversation = conversationService.findFriendsNotInConversation(token, conversationId);
            return ResponseEntity.ok(friendsNotInConversation);
        } catch (Exception e) {
            // Log the exception details as needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
