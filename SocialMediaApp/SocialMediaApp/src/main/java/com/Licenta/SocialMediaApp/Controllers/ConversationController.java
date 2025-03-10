package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Exceptions.ConversationAlreadyExistsException;
import com.Licenta.SocialMediaApp.Model.BodyResponse.ConversationResponse;
import com.Licenta.SocialMediaApp.Model.BodyResponse.MessageResponse;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.Message;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.MessageRepository;
import com.Licenta.SocialMediaApp.Service.ConversationService;
import com.Licenta.SocialMediaApp.Service.MessageService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/conversation")
public class ConversationController {
    private final ConversationService conversationService;
    private final MessageService messageService;
    private final MessageRepository messageRepository;

    public ConversationController(ConversationService conversationService, MessageService messageService, MessageRepository messageRepository) {
        this.conversationService = conversationService;
        this.messageService = messageService;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/all")
    public List<ConversationResponse> getUserConversations(@RequestHeader("Authorization")String jwt) {
         return conversationService.getUsersConversation(jwt);
    }

    @PostMapping("/private/create")
    public ResponseEntity<?> createPrivateConversationControllerMethod(
            @RequestParam Long userId,
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
            @RequestParam List<Long> members,
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
            @PathVariable Long conversationId,
            @RequestParam Long userId) {
        try {
            conversationService.addGroupMember(conversationId, userId);
            return ResponseEntity.ok().body("User added to the group successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding user to the group: " + e.getMessage());
        }
    }
    @DeleteMapping("/group/{conversationId}/members/remove/{userId}")
    public ResponseEntity<?> removeGroupMember(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {
        try {
            conversationService.removeGroupMember(conversationId, userId);
            return ResponseEntity.ok().body("User removed from the group successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error removing user from the group: " + e.getMessage());
        }
    }
    @DeleteMapping("/group/{conversationId}/leave")
    public ResponseEntity<?> leaveGroup(@PathVariable Long conversationId, @RequestHeader("Authorization") String jwt) {
        try {
            conversationService.leaveGroup(conversationId, jwt);
            return ResponseEntity.ok().body("Left group successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error leaving the group: " + e.getMessage());
        }
    }
    @GetMapping("/{conversationId}/image")
    public ResponseEntity<?> loadConversationImage(@PathVariable Long conversationId, @RequestHeader("Authorization") String jwt)
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
    public ResponseEntity<Page<MessageResponse>> getMessagesByConversationId(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Message> messagesPage = messageService.getMessagesByConversationId(conversationId, PageRequest.of(page, size));
            Page<MessageResponse> messageResponsesPage = messagesPage.map(Utils::convertToMessageResponse);
            return new ResponseEntity<>(messageResponsesPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{conversationId}/members")
    public ResponseEntity<List<UserResponse>> getConversationMembers(@PathVariable Long conversationId) {
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
    public ResponseEntity<List<UserResponse>> getFriendsNotInConversation(@PathVariable Long conversationId,
                                                                          @RequestHeader("Authorization") String token) {

        try {
            List<UserResponse> friendsNotInConversation = conversationService.findFriendsNotInConversation(token, conversationId);
            return ResponseEntity.ok(friendsNotInConversation);
        } catch (Exception e) {
            // Log the exception details as needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*@GetMapping("/{conversationId}/content")
    public List<Object> getConversationContent(@PathVariable Long conversationId) {
        return conversationService.getConversationContent(conversationId);
    }*/

    @GetMapping("/media")
    public ResponseEntity<?> getMessageMedia(@RequestParam String filePath) {
        try {
            System.out.println("Message Media");
            byte[] mediaBytes = messageService.getMessageMedia(filePath);
            //String mediaKey = messageService.getMediaKey(messageId);
            String mediaType = getMediaTypeFromKey(filePath);

            /*if (filePath.isEmpty() || filePath == null) {
                return ResponseEntity.notFound().build();
            }
*/
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mediaType))
                    .body(new ByteArrayResource(mediaBytes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }

    private String getMediaTypeFromKey(String key) {
        if (key.endsWith(".mp4")) {
            return "video/mp4";
        } else if (key.endsWith(".jpg") || key.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (key.endsWith(".png")) {
            return "image/png";
        }
        return "application/octet-stream"; // Default or unknown file types
    }
    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("conversationId") Long conversationId,
                                        @RequestParam("senderId") Long senderId,
                                        @RequestParam(value = "textContent", required = false) String textContent) {
        try {
            String key = messageService.sendFile(file, textContent, conversationId, senderId);
            Map<String, String> response = new HashMap<>();
            response.put("filePath", key); // Return the file path as a JSON object
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }

}
