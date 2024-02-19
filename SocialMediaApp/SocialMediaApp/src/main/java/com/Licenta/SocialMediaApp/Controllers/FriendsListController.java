package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.FriendsListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendsList")
public class FriendsListController {
    private final FriendsListService friendsListService;

    public FriendsListController(FriendsListService friendsListService) {
        this.friendsListService = friendsListService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Integer> getNrOfFriends(@PathVariable int userId) {
        try {
            int friendsNr = friendsListService.countNrOfFriends(userId);
            return ResponseEntity.ok(friendsNr);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/checkFriendship")
    public ResponseEntity<Boolean> checkIfUsersAreFriends(@RequestParam int userId1, @RequestParam int userId2) {
        try {
            boolean areFriends = friendsListService.isFriendshipExists(userId1, userId2);
            return ResponseEntity.ok(areFriends);
        } catch (Exception e) {
            // Handle exceptions appropriately
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/userFriends/{userId}")
    public List<User> getAllFriends(@PathVariable int userId) {
        return friendsListService.findFriendsByUserId(userId);
    }
    @DeleteMapping("/delete/{userid}")
    public ResponseEntity<String> deleteFriend(@RequestHeader("Authorization")String jwt,@PathVariable int userid)
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
