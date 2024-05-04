package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.UserService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final UserService userService;

    public SearchController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String username) {
        List<User> users = userService.findByUsernameContainingIgnoreCase(username);
        List<UserResponse> responses = users.stream()
                .map(Utils::convertToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

}
