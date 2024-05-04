package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    int countByUserId(int userId);
    List<Post> findByUser_Id(int userId);
}
