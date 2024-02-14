package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.User;

import java.util.List;

public interface FriendshipRequestService {
    List<User> findFriendshipRequestsSenders(String jwt);
    int getNrOfFrienshipRequests(String jwt);
    void declineFriendshipRequest(String jwt, int senderId);
    void acceptFriendshipRequest(String jwt, int senderId);
}
