package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.BlockListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<User> getBlockedUsers(@PathVariable int userId) {
       return blockListService.getBlockedUsersByUserId(userId);
    }
}
