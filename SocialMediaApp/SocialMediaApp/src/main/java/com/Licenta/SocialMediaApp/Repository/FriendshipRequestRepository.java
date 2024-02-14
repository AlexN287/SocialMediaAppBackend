package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.FriendshipRequest;
import com.Licenta.SocialMediaApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequest, Integer> {
    Optional<FriendshipRequest> findBySenderIdAndReceiverId(int senderId, int receiverId);
    @Query("SELECT fr.sender FROM FriendshipRequest fr WHERE fr.receiver.id = :receiverId AND fr.status = 'PENDING'")
    List<User> findSendersByReceiverIdWithPendingStatus(int receiverId);
    @Query("SELECT COUNT(fr) FROM FriendshipRequest fr WHERE fr.receiver.id = :receiverId AND fr.status = 'PENDING'")
    int countPendingFriendshipRequests(int receiverId);

}
