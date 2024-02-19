package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Model.User;

import java.util.List;

public interface FriendsListService {
    int countNrOfFriends(int userId);
    boolean isFriendshipExists(int userId1, int userId2);
    List<User> findFriendsByUserId(int userId);
    FriendsList createFriendsList(FriendsList friendsList);
    void deleteFriend(String jwt, int userId);
}
