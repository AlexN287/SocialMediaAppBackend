package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Content;
import com.Licenta.SocialMediaApp.Model.Like;
import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class LikeRepositoryTests {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ContentRepository contentRepository;

    private User user;
    private Post post;
    private Like like;

    @BeforeEach
    public void setUp() {
        // Initialize and save User
        user = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user = userRepository.save(user);

        // Initialize and save Post
        post = new Post();
        Content content= new Content("Post content", "/filePath");
        contentRepository.save(content);
        post.setContent(content);
        post = postRepository.save(post);

        // Initialize and save Like
        like = new Like(user, post);
        like = likeRepository.save(like);
    }

    @Test
    public void testExistsByUserAndPost() {
        // When
        boolean exists = likeRepository.existsByUserAndPost(user, post);

        // Then
        assertTrue(exists);
    }

    @Test
    public void testFindByUserAndPost() {
        // When
        Like foundLike = likeRepository.findByUserAndPost(user, post);

        // Then
        assertNotNull(foundLike);
        assertEquals(like.getId(), foundLike.getId());
    }

    @Test
    public void testCountByPostId() {
        // When
        long count = likeRepository.countByPostId(post.getId());

        // Then
        assertEquals(1, count);
    }

    @Test
    public void testFindUsersByPostId() {
        // When
        List<User> users = likeRepository.findUsersByPostId(post.getId());

        // Then
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getId(), users.get(0).getId());
    }

    @Test
    public void testDeleteByPostId() {
        // When
        likeRepository.deleteByPostId(post.getId());

        // Then
        List<Like> likes = likeRepository.findAll();
        assertTrue(likes.isEmpty());
    }
}