package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.BodyResponse.LikeResponse;
import com.Licenta.SocialMediaApp.Model.Like;
import com.Licenta.SocialMediaApp.Service.LikeService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<?> addLike(@PathVariable int postId, @RequestHeader("Authorization") String jwt) {
        try {
            Like like = likeService.addLike(jwt, postId);
            LikeResponse likeResponse = Utils.convertToLikeResponse(like);
            return new ResponseEntity<>(likeResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteLike(@PathVariable int postId, @RequestHeader("Authorization") String jwt) {
        try {
            likeService.deleteLike(jwt, postId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{postId}/check")
    public ResponseEntity<?> checkLike(@RequestHeader("Authorization") String jwt, @PathVariable int postId) {
        try {
            boolean hasLiked = likeService.checkUserLikedPost(jwt, postId);
            return ResponseEntity.ok(hasLiked);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while checking the like status.");
        }
    }
}
