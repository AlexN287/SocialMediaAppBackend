package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Repository.PostRepository;
import com.Licenta.SocialMediaApp.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Integer> getNrOfFriends(@PathVariable int userId) {
        try {
            int postNr = postService.getPostsNrOfUser(userId);
            return ResponseEntity.ok(postNr);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
