package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.FriendshipRequest;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.FriendshipRequestRepository;
import com.Licenta.SocialMediaApp.Service.FriendshipRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendship")
public class FrienshipRequestController {
    private final FriendshipRequestRepository friendshipRequestRepository;
    private final FriendshipRequestService friendshipRequestService;
    public FrienshipRequestController(FriendshipRequestRepository friendshipRequestRepository, FriendshipRequestService friendshipRequestService) {
        this.friendshipRequestRepository = friendshipRequestRepository;
        this.friendshipRequestService = friendshipRequestService;
    }

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
    @GetMapping("/requests")
    public List<UserResponse> getFriendshipRequestSenders(@RequestHeader("Authorization")String jwt) {
        return friendshipRequestService.findFriendshipRequestsSenders(jwt);
    }
    @GetMapping("/requestsNr")
    public int getNrOfFrienshipRequests(@RequestHeader("Authorization")String jwt)
    {
        return friendshipRequestService.getNrOfFrienshipRequests(jwt);
    }

    @PatchMapping("/declineRequest")
    public ResponseEntity<String> declineFriendshipRequest(@RequestHeader("Authorization")String jwt, @RequestParam int senderId)
    {
        try {
            friendshipRequestService.declineFriendshipRequest(jwt, senderId);
            return ResponseEntity.ok("Friendship request declined");
        } catch (Exception e)
        {
            return ResponseEntity.internalServerError().body("Decline friendship request failed");
        }
    }

    @PostMapping("/acceptRequest")
    public ResponseEntity<String> acceptFriendshipRequest(@RequestHeader("Authorization")String jwt, @RequestParam int senderId)
    {
        try {
            friendshipRequestService.acceptFriendshipRequest(jwt, senderId);
            return ResponseEntity.ok("Friendship request accepted");
        } catch (Exception e)
        {
            return ResponseEntity.internalServerError().body("Acccept friendship request failed");
        }
    }

}
