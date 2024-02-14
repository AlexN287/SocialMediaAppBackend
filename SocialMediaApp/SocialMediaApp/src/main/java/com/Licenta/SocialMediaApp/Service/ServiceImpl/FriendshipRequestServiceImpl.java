package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Model.FriendsListId;
import com.Licenta.SocialMediaApp.Model.FriendshipRequest;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.FriendsListRepository;
import com.Licenta.SocialMediaApp.Repository.FriendshipRequestRepository;
import com.Licenta.SocialMediaApp.Service.FriendsListService;
import com.Licenta.SocialMediaApp.Service.FriendshipRequestService;
import com.Licenta.SocialMediaApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendshipRequestServiceImpl implements FriendshipRequestService {
    private final FriendshipRequestRepository friendshipRequestRepository;
    private final UserService userService;
    private final FriendsListService friendsListService;
    public FriendshipRequestServiceImpl(FriendshipRequestRepository friendshipRequestRepository, UserService userService, FriendsListService friendsListService) {
        this.friendshipRequestRepository = friendshipRequestRepository;
        this.userService = userService;
        this.friendsListService = friendsListService;
    }

    @Override
    public List<User> findFriendshipRequestsSenders(String jwt) {
        User receiver = userService.findUserByJwt(jwt);

        List<User> senders = friendshipRequestRepository.findSendersByReceiverIdWithPendingStatus(receiver.getId());
        return senders.stream().map(this::clearSensitiveInformation).collect(Collectors.toList());
    }

    @Override
    public int getNrOfFrienshipRequests(String jwt) {
        User receiver = userService.findUserByJwt(jwt);

        return friendshipRequestRepository.countPendingFriendshipRequests(receiver.getId());
    }

    @Override
    public void declineFriendshipRequest(String jwt, int senderId) {
        User receiver = userService.findUserByJwt(jwt);

        FriendshipRequest friendshipRequest = friendshipRequestRepository.findBySenderIdAndReceiverId(senderId, receiver.getId())
                .orElseThrow(() -> new RuntimeException("Request not found"));

        friendshipRequest.setStatus("DECLINED");
        friendshipRequestRepository.save(friendshipRequest);
    }

    @Override
    public void acceptFriendshipRequest(String jwt, int senderId) {
        User receiver = userService.findUserByJwt(jwt);

        FriendshipRequest friendshipRequest = friendshipRequestRepository.findBySenderIdAndReceiverId(senderId, receiver.getId())
                .orElseThrow(() -> new RuntimeException("Request not found"));

        friendshipRequest.setStatus("ACCEPTED");
        friendshipRequestRepository.save(friendshipRequest);

        FriendsList friendsList = new FriendsList();
        FriendsListId friendsListId = new FriendsListId();
        friendsListId.setUser1(receiver);
        User user2 = new User();
        user2.setId(senderId);
        friendsListId.setUser2(user2);
        friendsList.setId(friendsListId);
        friendsListService.createFriendsList(friendsList);
    }

    private User clearSensitiveInformation(User user) {
        user.setPassword(null); // Clear the password
        user.setProfileImagePath(null); // Clear the profile image path
        return user;
    }
}
