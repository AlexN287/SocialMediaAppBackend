package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Repository.FriendsListRepository;
import com.Licenta.SocialMediaApp.Service.FriendsListService;
import org.springframework.beans.factory.annotation.Autowired;
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

    /*@PostMapping
    public ResponseEntity<FriendsList> createFriendsList(@RequestBody FriendsList friendsList) {
        try {
            FriendsList createdFriendsList = friendsListService.createFriendsList(friendsList);
            return ResponseEntity.ok(createdFriendsList);
        } catch (Exception e) {
            // Handle exceptions appropriately
            return ResponseEntity.status(500).build();
        }
    }*/
    @GetMapping("/userFriends/{userId}")
    public List<FriendsList> getAllFriends(@PathVariable int userId) {
        return friendsListService.findFriendsByUserId(userId);
    }

}
