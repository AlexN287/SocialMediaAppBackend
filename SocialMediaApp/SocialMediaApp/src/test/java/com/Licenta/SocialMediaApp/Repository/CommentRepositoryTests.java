package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Comment;
import com.Licenta.SocialMediaApp.Model.Content;
import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CommentRepositoryTests {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ContentRepository contentRepository;

    private User user;
    private Post post;
    private Content content;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        // Initialize and save User
        user = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user = userRepository.save(user);

        // Initialize and save Post
        post = new Post();
        post.setUser(user);
        Content postContent = new Content("Post content", "/post/path");
        contentRepository.save(postContent);
        post.setContent(postContent);
        post = postRepository.save(post);

        // Initialize and save Content
        content = new Content();
        content.setTextContent("Comment content");
        content.setFilePath("/comment/path");
        content = contentRepository.save(content);

        // Initialize and save Comment
        comment = new Comment(user, post, content, LocalDateTime.now());
        comment = commentRepository.save(comment);
    }

    @Test
    public void testCountByPostId() {
        // When
        long count = commentRepository.countByPostId(post.getId());

        // Then
        assertEquals(1, count);
    }

    @Test
    public void testFindByPostId() {
        // When
        List<Comment> comments = commentRepository.findByPostId(post.getId());

        // Then
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.get(0).getId());
    }

    @Test
    public void testDeleteByPostId() {
        // When
        commentRepository.deleteByPostId(post.getId());

        // Then
        List<Comment> comments = commentRepository.findByPostId(post.getId());
        assertTrue(comments.isEmpty());
    }
}
