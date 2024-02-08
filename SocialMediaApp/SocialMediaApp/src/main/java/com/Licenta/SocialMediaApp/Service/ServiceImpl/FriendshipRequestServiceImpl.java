package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Repository.FriendshipRequestRepository;
import com.Licenta.SocialMediaApp.Service.FriendshipRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendshipRequestServiceImpl implements FriendshipRequestService {
    private final FriendshipRequestRepository friendshipRequestRepository;

    @Autowired
    public FriendshipRequestServiceImpl(FriendshipRequestRepository friendshipRequestRepository) {
        this.friendshipRequestRepository = friendshipRequestRepository;
    }
}
