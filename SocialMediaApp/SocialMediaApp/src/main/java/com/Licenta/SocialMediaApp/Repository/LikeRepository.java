package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Like;
import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndPost(User user, Post post);
    Like findByUserAndPost(User user, Post post);
    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId")
    long countByPostId(Long postId);
    @Query("SELECT l.user FROM Like l WHERE l.post.id = :postId")
    List<User> findUsersByPostId(@Param("postId") Long postId);
    void deleteByPostId(Long postId);
}
