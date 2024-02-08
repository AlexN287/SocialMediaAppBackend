package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.FriendshipRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequest, Integer> {
    Optional<FriendshipRequest> findBySenderIdAndReceiverId(int senderId, int receiverId);
}
