package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.BodyResponse.CommentResponse;
import com.Licenta.SocialMediaApp.Model.Comment;
import com.Licenta.SocialMediaApp.Service.CommentService;
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
    public ResponseEntity<Comment> addComment(@PathVariable int postId, @RequestParam String commentText, @RequestHeader("Authorization") String jwt) {
        try {
            Comment comment = commentService.addComment(jwt, postId, commentText);
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int commentId, @RequestHeader("Authorization") String jwt) {
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
    public ResponseEntity<Long> getCommentCount(@PathVariable int postId) {
        long commentCount = commentService.getCommentCountForPost(postId);
        return ResponseEntity.ok(commentCount);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable int postId) {
        List<CommentResponse> comments = commentService.getCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }
}
