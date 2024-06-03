package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Model.User;

import java.util.List;

public interface FriendsListService {
    int countNrOfFriends(Long userId);
    boolean isFriendshipExists(Long userId1, Long userId2);
    List<User> findFriendsByUserId(Long userId);
    FriendsList createFriendsList(FriendsList friendsList);
    void deleteFriend(String jwt, Long userId);
}
