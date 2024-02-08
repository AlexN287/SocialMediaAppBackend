package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Repository.FriendsListRepository;
import com.Licenta.SocialMediaApp.Service.FriendsListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendsListServiceImpl implements FriendsListService {
    private final FriendsListRepository friendsListRepository;

    @Autowired
    public FriendsListServiceImpl(FriendsListRepository friendsListRepository) {
        this.friendsListRepository = friendsListRepository;
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
    public List<FriendsList> findFriendsByUserId(int userId) {
        return friendsListRepository.findFriendsByUserId(userId);
    }
    @Override
    public FriendsList createFriendsList(FriendsList friendsList) {
        return friendsListRepository.save(friendsList);
    }
}
