package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.BodyRequests.PasswordChangeRequest;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.UserService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserFromToken(@RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserByJwt(jwt); // Assumes userService will throw UserNotFoundException if not found
            UserResponse userResponse = Utils.convertToUserResponse(user);
            return ResponseEntity.ok(userResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            // It's a good practice to log the exception details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int userId) {
        try {
            User user = userService.findById(userId);
            UserResponse response = Utils.convertToUserResponse(user);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping("/{userId}/uploadProfileImage")
    public ResponseEntity<?> uploadProfileImage(@PathVariable int userId,
                                                @RequestParam("file") MultipartFile file) {
        try {
            userService.uploadUserProfileImage(userId, file);
            return ResponseEntity.ok("Profile image uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }
    @GetMapping("/{userId}/loadProfileImage")
    public ResponseEntity<?> getProfileImage(@PathVariable int userId) {
        try {
            System.out.println("Profile image");
            byte[] imageBytes = userService.getUserProfileImage(userId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=31536000")
                    .contentType(MediaType.IMAGE_JPEG) // Consider dynamically setting this based on the image data
                    .body(new ByteArrayResource(imageBytes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }

    @PatchMapping("/editUsername")
    public ResponseEntity<String> updateUsername(@RequestHeader("Authorization")String jwt, @RequestParam String newUsername) {
        try {
            String newJwt = userService.updateUsername(newUsername, jwt);
            return ResponseEntity.ok().body(newJwt);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Change username failed");
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String jwt, @RequestBody PasswordChangeRequest passwordChangeRequest) {
        try {
            userService.changePassword(passwordChangeRequest.getOldPassword(), passwordChangeRequest.getNewPassword(), jwt);
            return ResponseEntity.ok().body("Password changed successfully");
        } catch (BadCredentialsException e) {
            // Explicitly handle bad credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect old password");
        } catch (Exception e) {
            // Handle other exceptions more generically
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Password change failed due to an internal error");
        }
    }

    @GetMapping("/connectedFriends")
    public List<UserResponse> getConnectedFriends(@RequestHeader("Authorization") String jwt) {
        return userService.getConnectedFriends(jwt);
    }

}
