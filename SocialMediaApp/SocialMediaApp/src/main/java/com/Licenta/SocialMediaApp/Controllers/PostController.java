package com.Licenta.SocialMediaApp.Controllers;


import com.Licenta.SocialMediaApp.Model.BodyResponse.PostResponse;
import com.Licenta.SocialMediaApp.Model.BodyResponse.PostWithReports;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.Report;
import com.Licenta.SocialMediaApp.Service.PostService;
import com.Licenta.SocialMediaApp.Service.ReportService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;

    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<Integer> getNrOfPosts(@PathVariable int userId) {
        try {
            int postNr = postService.getPostsNrOfUser(userId);
            return ResponseEntity.ok(postNr);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    @PostMapping("/add")
    public ResponseEntity<?> createPost(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("text") String text) {
        try {
            Post post = postService.createPost(jwt, file, text);
            PostResponse postResponse = Utils.convertToPostResponse(post);
            return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable int postId,
            @RequestHeader("Authorization") String jwt) {
        try {
            postService.deletePost(postId, jwt);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<PostResponse>> getAllPostsByUser(@PathVariable int userId) {
        try {
            List<Post> posts = postService.getAllPostsByUser(userId);
            List<PostResponse> postResponses = posts.stream()
                    .map(Utils::convertToPostResponse)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(postResponses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Long> getLikesCount(@PathVariable int postId) {
        long likesCount = postService.getLikesCountForPost(postId);
        return ResponseEntity.ok(likesCount);
    }

    @GetMapping("/{postId}/likes/users")
    public ResponseEntity<List<UserResponse>> getUsersWhoLiked(@PathVariable int postId) {
        List<UserResponse> users = postService.getUsersWhoLikedPost(postId);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/{postId}/media")
    public ResponseEntity<?> getPostMedia(@PathVariable int postId) {
        try {
            System.out.println("Post Media");
            byte[] mediaBytes = postService.getPostMedia(postId);
            String mediaType = getMediaTypeFromKey(postService.getMediaKey(postId));// Assuming method to get media type

            if(postService.getMediaKey(postId).isEmpty() || postService.getMediaKey(postId) == null)
            {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mediaType))
                    .body(new ByteArrayResource(mediaBytes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }
    private String getMediaTypeFromKey(String key) {
        if (key.endsWith(".mp4")) {
            return "video/mp4";
        } else if (key.endsWith(".jpg") || key.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (key.endsWith(".png")) {
            return "image/png";
        }
        return "application/octet-stream"; // Default or unknown file types
    }
    @GetMapping("/users/friends/posts")
    public List<PostResponse> getFriendsPosts(@RequestHeader("Authorization") String jwt) {
        List<Post> posts = postService.getPostsByFriends(jwt);
        return posts.stream().map(Utils::convertToPostResponse).collect(Collectors.toList());
    }

    @PatchMapping("/{postId}/updateContent")
    public ResponseEntity<?> updatePostContent(
            @PathVariable int postId,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestHeader("Authorization") String jwt) {
        try {
            Post post = postService.updatePostContent(postId, content, file, jwt);
            PostResponse postResponse = Utils.convertToPostResponse(post);
            return ResponseEntity.ok(postResponse);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update post", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reported/ordered")
    public ResponseEntity<?> getPostsOrderedByReportCount() {
        try {
            List<Post> posts = postService.getPostsOrderedByReportCount();
            List<PostWithReports> postWithReportsList = posts.stream()
                    .map(Utils::convertToPostWithReports)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(postWithReportsList);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
