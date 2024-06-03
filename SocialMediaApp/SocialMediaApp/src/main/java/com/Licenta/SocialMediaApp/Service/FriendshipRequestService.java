package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.User;

import java.util.List;

public interface FriendshipRequestService {
    List<UserResponse> findFriendshipRequestsSenders(String jwt);
    int getNrOfFrienshipRequests(String jwt);
    void declineFriendshipRequest(String jwt, Long senderId);
    void acceptFriendshipRequest(String jwt, Long senderId);
}
