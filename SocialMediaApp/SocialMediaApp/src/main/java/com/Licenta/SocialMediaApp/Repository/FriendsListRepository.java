package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendsListRepository extends JpaRepository<FriendsList, Integer> {
    @Query("SELECT COUNT(f) FROM FriendsList f WHERE f.id.user1.id = :userId OR f.id.user2.id = :userId")
    int countNrOfFriends(int userId);
    @Query("SELECT COUNT(f) > 0 FROM FriendsList f WHERE (f.id.user1.id = ?1 AND f.id.user2.id = ?2) OR (f.id.user1.id = ?2 AND f.id.user2.id = ?1)")
    boolean isFriendshipExists(int userId1, int userId2);
    /*@Query("SELECT f FROM FriendsList f WHERE f.id.user1.id = :userId OR f.id.user2.id = :userId")
    List<FriendsList> findFriendsByUserId(int userId);*/
    @Query("SELECT f.id.user2 FROM FriendsList f WHERE f.id.user1.id = :userId " +
            "UNION " +
            "SELECT f.id.user1 FROM FriendsList f WHERE f.id.user2.id = :userId")
    List<User> findFriendsByUserId(@Param("userId") int userId);
    @Query("SELECT fl FROM FriendsList fl WHERE " +
            "(fl.id.user1.id = :userId1 AND fl.id.user2.id = :userId2) OR " +
            "(fl.id.user1.id = :userId2 AND fl.id.user2.id = :userId1)")
    Optional<FriendsList> findByUsers(@Param("userId1") int userId1, @Param("userId2") int userId2);
}
