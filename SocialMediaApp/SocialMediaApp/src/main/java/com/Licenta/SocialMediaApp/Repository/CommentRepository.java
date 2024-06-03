package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countByPostId(Long postId);
    List<Comment> findByPostId(Long postId);
    void deleteByPostId(Long postId);
}
