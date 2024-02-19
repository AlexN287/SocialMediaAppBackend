package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.FriendsListRepository;
import com.Licenta.SocialMediaApp.Service.FriendsListService;
import com.Licenta.SocialMediaApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendsListServiceImpl implements FriendsListService {
    private final FriendsListRepository friendsListRepository;
    private final UserService userService;
    @Autowired
    public FriendsListServiceImpl(FriendsListRepository friendsListRepository, UserService userService) {
        this.friendsListRepository = friendsListRepository;
        this.userService = userService;
    }

    @Override
    public int countNrOfFriends(int userId) {
        return friendsListRepository.countNrOfFriends(userId);
    }
    @Override
    public boolean isFriendshipExists(int userId1, int userId2) {
        return friendsListRepository.isFriendshipExists(userId1, userId2);
    }
    @Override
    public List<User> findFriendsByUserId(int userId) {
        List<User> friends = friendsListRepository.findFriendsByUserId(userId);
        for (User friend : friends) {
            friend.setPassword(null);
            friend.setProfileImagePath(null);
        }
        return friends;
    }
    @Override
    public FriendsList createFriendsList(FriendsList friendsList) {
        return friendsListRepository.save(friendsList);
    }

    @Override
    public void deleteFriend(String jwt, int userId) {
        User loggedUser = userService.findUserByJwt(jwt);

        FriendsList friendsList = friendsListRepository.findByUsers(loggedUser.getId(), userId)
                .orElseThrow(() -> new RuntimeException("FriendList not found"));

        friendsListRepository.delete(friendsList);
    }
}
