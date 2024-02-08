package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.FriendsList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendsListRepository extends JpaRepository<FriendsList, Integer> {
    @Query("SELECT COUNT(f) FROM FriendsList f WHERE f.id.user1.id = :userId OR f.id.user2.id = :userId")
    int countNrOfFriends(int userId);
    @Query("SELECT COUNT(f) > 0 FROM FriendsList f WHERE (f.id.user1.id = ?1 AND f.id.user2.id = ?2) OR (f.id.user1.id = ?2 AND f.id.user2.id = ?1)")
    boolean isFriendshipExists(int userId1, int userId2);
    @Query("SELECT f FROM FriendsList f WHERE f.id.user1.id = :userId OR f.id.user2.id = :userId")
    List<FriendsList> findFriendsByUserId(int userId);
}
