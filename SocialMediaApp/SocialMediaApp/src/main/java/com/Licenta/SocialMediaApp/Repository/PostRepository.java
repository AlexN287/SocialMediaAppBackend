package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    int countByUserId(Long userId);
    List<Post> findByUser_Id(Long userId);
    @Query("SELECT p FROM Post p WHERE p.user.id IN (SELECT f.id.user2.id FROM FriendsList f WHERE f.id.user1.id = :userId) OR p.user.id IN (SELECT f.id.user1.id FROM FriendsList f WHERE f.id.user2.id = :userId) ORDER BY p.createdAt DESC")
    List<Post> findAllPostsByFriends(@Param("userId") Long userId);
    @Query("SELECT p FROM Post p LEFT JOIN p.reports r GROUP BY p.id HAVING COUNT(r.id) > 0 ORDER BY COUNT(r.id) DESC")
    List<Post> findAllPostsOrderedByReportCount();

}
