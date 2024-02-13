package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Bucket;
import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Model.BodyRequests.PasswordChangeRequest;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private S3Service s3Service;
    @Autowired
    private S3Bucket s3Bucket;

    @GetMapping("/profile")
    public User getUserFromToken(@RequestHeader("Authorization")String jwt)
    {
        User user = userService.findUserByJwt(jwt);
        user.setPassword(null);
        return user;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable int userId) throws Exception {
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/uploadProfileImage")
    public ResponseEntity<?> uploadProfileImage(@PathVariable int userId,
                                                @RequestParam("file") MultipartFile file) {
        try {
            // Ensure the user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("User not found"));

            // Upload the profile image
            String profileImageKey = s3Service.generateProfileImageKey(userId, file);
            s3Service.putObject(profileImageKey, file.getBytes());

            // Update user's profile image path in the database
            user.setProfileImagePath(profileImageKey);
            userRepository.save(user);

            return ResponseEntity.ok("Profile image uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/loadProfileImage")
    public ResponseEntity<?> getProfileImage(@PathVariable int userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("User not found"));

            String key = user.getProfileImagePath();
            byte[] imageBytes = s3Service.getObject(s3Bucket.getBucket(), key);
            System.out.println("Profile Image!!!!!!!!");

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Set appropriate content type based on the image
                    .body(new ByteArrayResource(imageBytes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/deleteAccount")
    public void deleteUser(@PathVariable int userId) {
        userRepository.deleteById(userId);
    }

    @PatchMapping("/editUsername")
    public String updateUsername(@RequestHeader("Authorization")String jwt, @RequestParam String newUsername) {
        try {
            userService.updateUsername(newUsername, jwt);
            return "Username updated successfully.";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization")String jwt, @RequestBody PasswordChangeRequest passwordChangeRequest)
    {
        try {
            userService.changePassword(passwordChangeRequest.getOldPassword(), passwordChangeRequest.getNewPassword(), jwt);
            return ResponseEntity.ok().body("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Password change failed");
        }
    }
}
