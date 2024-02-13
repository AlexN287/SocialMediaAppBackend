package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.FriendshipRequest;
import com.Licenta.SocialMediaApp.Repository.FriendshipRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friendship")
public class FrienshipRequestController {
    @Autowired
    private FriendshipRequestRepository friendshipRequestRepository;

    @PostMapping("/sendRequest")
    public ResponseEntity<FriendshipRequest> requestFriendship(@RequestBody FriendshipRequest request) {
        try {
            FriendshipRequest savedRequest = friendshipRequestRepository.save(request);
            return new ResponseEntity<>(savedRequest, HttpStatus.CREATED); // Return 200 OK with the saved request
        } catch (Exception e) {
            // You can handle different exceptions and return different responses accordingly
            return ResponseEntity.status(500).body(null); // Example for a server error
        }
    }

    @GetMapping("/exists/{senderId}/{receiverId}")
    public boolean checkFriendshipExists(@PathVariable int senderId, @PathVariable int receiverId) {
        return friendshipRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId).isPresent();
    }
}
