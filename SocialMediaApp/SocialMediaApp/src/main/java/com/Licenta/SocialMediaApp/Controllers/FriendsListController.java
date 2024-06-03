package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.FriendsListService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/friendsList")
public class FriendsListController {
    private final FriendsListService friendsListService;

    public FriendsListController(FriendsListService friendsListService) {
        this.friendsListService = friendsListService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Integer> getNrOfFriends(@PathVariable Long userId) {
        try {
            int friendsNr = friendsListService.countNrOfFriends(userId);
            return ResponseEntity.ok(friendsNr);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/checkFriendship")
    public ResponseEntity<Boolean> checkIfUsersAreFriends(@RequestParam Long userId1, @RequestParam Long userId2) {
        try {
            boolean areFriends = friendsListService.isFriendshipExists(userId1, userId2);
            return ResponseEntity.ok(areFriends);
        } catch (Exception e) {
            // Handle exceptions appropriately
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/userFriends/{userId}")
    public ResponseEntity<List<UserResponse>> getAllFriends(@PathVariable Long userId) {
        try {
            List<User> friends = friendsListService.findFriendsByUserId(userId);
            List<UserResponse> userResponses = friends.stream()
                    .map(Utils::convertToUserResponse)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userResponses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete/{userid}")
    public ResponseEntity<String> deleteFriend(@RequestHeader("Authorization")String jwt,@PathVariable Long userid)
    {
        try {
            friendsListService.deleteFriend(jwt, userid);
            return ResponseEntity.ok().body("Friend deleted successfully.");
        } catch (Exception e)
        {
            return ResponseEntity.internalServerError().body("Failed to delete friend");
        }
    }

}
