package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.BodyResponse.CommentResponse;
import com.Licenta.SocialMediaApp.Model.Comment;
import com.Licenta.SocialMediaApp.Service.CommentService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long postId, @RequestParam String commentText, @RequestHeader("Authorization") String jwt) {
        try {
            Comment comment = commentService.addComment(jwt, postId, commentText);
            CommentResponse commentResponse = Utils.convertToCommentResponse(comment);
            return new ResponseEntity<>(commentResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, @RequestHeader("Authorization") String jwt) {
        try {
            commentService.deleteComment(commentId, jwt);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable Long postId) {
        long commentCount = commentService.getCommentCountForPost(postId);
        return ResponseEntity.ok(commentCount);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        List<CommentResponse> comments = commentService.getCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }
    @PatchMapping("/edit/{commentId}")
    public ResponseEntity<?> updateCommentContent(
            @PathVariable Long commentId,
            @RequestParam("text") String newText,
            @RequestHeader("Authorization") String jwt) {
        try {
            Comment updatedComment = commentService.updateCommentText(commentId, newText, jwt);
            CommentResponse commentResponse = Utils.convertToCommentResponse(updatedComment);
            return ResponseEntity.ok(commentResponse);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
