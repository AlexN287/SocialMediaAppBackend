package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Service.ModeratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/moderator")
public class ModeratorController {
    private final ModeratorService moderatorService;

    public ModeratorController(ModeratorService moderatorService) {
        this.moderatorService = moderatorService;
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkIfUserIsModerator(@RequestHeader("Authorization") String jwt) {
        try {
            boolean isModerator = moderatorService.isModerator(jwt);
            return ResponseEntity.ok(isModerator);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode().value()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}
