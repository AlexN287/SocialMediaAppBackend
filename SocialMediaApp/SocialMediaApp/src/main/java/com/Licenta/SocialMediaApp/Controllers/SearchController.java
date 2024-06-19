package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.BodyResponse.ConversationResponse;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserWithRoles;
import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.AdminService;
import com.Licenta.SocialMediaApp.Service.ConversationService;
import com.Licenta.SocialMediaApp.Service.UserService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final UserService userService;
    private final AdminService adminService;
    private final ConversationService conversationService;
    public SearchController(UserService userService, AdminService adminService, ConversationService conversationService) {
        this.userService = userService;
        this.adminService = adminService;
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String username, @RequestHeader("Authorization") String jwt) {
        List<User> users = userService.searchByUsernameExcludingLoggedInUser(username, jwt);
        List<UserResponse> responses = users.stream()
                .map(Utils::convertToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserWithRoles>> searchUsersAsAdmin(@RequestParam String username, @RequestHeader("Authorization") String jwt) {
        if (!adminService.isAdmin(jwt)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
        }


        List<User> users = userService.findByUsernameContainingIgnoreCase(username);
        List<UserWithRoles> responses = users.stream()
                .map(Utils::convertToUserWithRoles)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/conversation/user")
    public ResponseEntity<List<ConversationResponse>> searchUserConversations(@RequestHeader("Authorization") String jwt, @RequestParam String term) {
        List<ConversationResponse> conversations = conversationService.searchUsersConversation(jwt, term);
        return ResponseEntity.ok(conversations);
    }

}
