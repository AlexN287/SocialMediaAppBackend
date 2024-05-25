package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserWithRoles;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.AdminService;
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
    public SearchController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String username) {
        List<User> users = userService.findByUsernameContainingIgnoreCase(username);
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

}
