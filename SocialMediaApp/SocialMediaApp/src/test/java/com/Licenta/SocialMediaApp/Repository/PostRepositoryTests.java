package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class PostRepositoryTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private FriendsListRepository friendsListRepository;

    private User user1;
    private User user2;
    private Post post1;
    private Post post2;

    @BeforeEach
    public void setUp() {
        // Initialize and save Users
        user1 = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user1 = userRepository.save(user1);

        user2 = new User("jane_doe", "password456", "jane_doe@example.com", "/profile/path2");
        user2 = userRepository.save(user2);

        // Initialize and save Content
        Content content1 = new Content();
        content1.setTextContent("Post 1");
        content1.setFilePath("/filePath1");
        content1 = contentRepository.save(content1);

        Content content2 = new Content();
        content2.setTextContent("Post 2");
        content2.setFilePath("/filePath2");
        content2 = contentRepository.save(content2);

        // Initialize and save Posts
        post1 = new Post(user1, content1);
        post1.setCreatedAt(LocalDateTime.now());
        post1 = postRepository.save(post1);

        post2 = new Post(user2, content2);
        post2.setCreatedAt(LocalDateTime.now());
        post2 = postRepository.save(post2);
    }

    @Test
    public void testCountByUserId() {
        // When
        int count = postRepository.countByUserId(user1.getId());

        // Then
        assertEquals(1, count);
    }

    @Test
    public void testFindByUserId() {
        // When
        List<Post> posts = postRepository.findByUser_Id(user1.getId());

        // Then
        assertNotNull(posts);
        assertEquals(1, posts.size());
    }

    @Test
    public void testFindAllPostsByFriends() {
        // Simulate friendship
        FriendsList friendsList = new FriendsList();
        FriendsListId friendsListId = new FriendsListId();
        friendsListId.setUser1(user1);
        friendsListId.setUser2(user2);
        friendsList.setId(friendsListId);
        friendsListRepository.save(friendsList);

        // When
        List<Post> posts = postRepository.findAllPostsByFriends(user1.getId());

        // Then
        assertNotNull(posts);
        assertEquals(1, posts.size());
    }

    @Test
    public void testFindAllPostsOrderedByReportCount() {
        // Given
        Report report1 = new Report();
        report1.setReason("Reason 1");
        report1.setPost(post1);
        report1.setReportTime(LocalDateTime.now());

        Report report2 = new Report();
        report2.setReason("Reason 2");
        report2.setPost(post1);
        report2.setReportTime(LocalDateTime.now());

        Report report3 = new Report();
        report3.setReason("Reason 3");
        report3.setPost(post2);
        report3.setReportTime(LocalDateTime.now());

        reportRepository.save(report1);
        reportRepository.save(report2);
        reportRepository.save(report3);

        // When
        List<Post> posts = postRepository.findAllPostsOrderedByReportCount();

        // Then
        assertNotNull(posts);
        assertEquals(2, posts.size());
        assertEquals(post1.getId(), posts.get(0).getId());
        assertEquals(post2.getId(), posts.get(1).getId());
    }
}