package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.BlockListService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blockList")
public class BlockListController {
    private final BlockListService blockListService;

    public BlockListController(BlockListService blockListService) {
        this.blockListService = blockListService;
    }

    @PostMapping("/blockUser/{blockedUserId}")
    public ResponseEntity<?> blockUser(@RequestHeader("Authorization")String jwt, @PathVariable int blockedUserId) {
        boolean blocked = blockListService.blockUser(jwt, blockedUserId);

        if (blocked) {
            return ResponseEntity.ok().body("User blocked successfully.");
        } else {
            return ResponseEntity.badRequest().body("User is already blocked.");
        }
    }

    @DeleteMapping("/unblock/{blockedUserId}")
    public ResponseEntity<?> unblockUser(@RequestHeader("Authorization")String jwt, @PathVariable int blockedUserId) {
        boolean unblocked = blockListService.unblockUser(jwt, blockedUserId);

        if (unblocked) {
            return ResponseEntity.ok().body("User unblocked successfully.");
        } else {
            return ResponseEntity.badRequest().body("No such block exists.");
        }
    }

    @GetMapping("/blockedBy/{userId}")
    public ResponseEntity<List<UserResponse>> getBlockedUsers(@PathVariable int userId) {
        try {
            List<User> blockedUsers = blockListService.getBlockedUsersByUserId(userId);
            List<UserResponse> userResponses = blockedUsers.stream()
                    .map(Utils::convertToUserResponse)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userResponses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/isBlockedBy/{otherUserId}")
    public ResponseEntity<Boolean> isUserBlockedBy(@RequestHeader("Authorization") String jwt, @PathVariable int otherUserId) {
        try {
            boolean isBlocked = blockListService.isUserBlockedBy(jwt, otherUserId);
            return ResponseEntity.ok(isBlocked);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
