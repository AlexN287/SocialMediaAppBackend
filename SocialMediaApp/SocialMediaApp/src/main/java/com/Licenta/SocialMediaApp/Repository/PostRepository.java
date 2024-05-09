package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    int countByUserId(int userId);
    List<Post> findByUser_Id(int userId);
    @Query("SELECT p FROM Post p WHERE p.user.id IN (SELECT f.id.user2.id FROM FriendsList f WHERE f.id.user1.id = :userId) OR p.user.id IN (SELECT f.id.user1.id FROM FriendsList f WHERE f.id.user2.id = :userId) ORDER BY p.createdAt DESC")
    List<Post> findAllPostsByFriends(@Param("userId") int userId);
}
