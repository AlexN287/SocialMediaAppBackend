package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.Content;
import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.CommentRepository;
import com.Licenta.SocialMediaApp.Repository.ContentRepository;
import com.Licenta.SocialMediaApp.Repository.LikeRepository;
import com.Licenta.SocialMediaApp.Repository.PostRepository;
import com.Licenta.SocialMediaApp.Service.ServiceImpl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private ModeratorService moderatorService;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post post;
    private Content content;

    @BeforeEach
    public void setUp() {
        // Initialize User
        user = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user.setId(1L);

        // Initialize Content
        content = new Content();
        content.setId(1L);
        content.setTextContent("Sample text");
        content.setFilePath("/file/path");

        // Initialize Post
        post = new Post();
        post.setId(1L);
        post.setUser(user);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());

        lenient().when(postRepository.save(any(Post.class))).thenReturn(post);
        lenient().when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        lenient().when(contentRepository.save(any(Content.class))).thenReturn(content);
        lenient().when(userService.findUserByJwt(anyString())).thenReturn(user);
    }

    @Test
    public void testGetPostsNrOfUser() {
        // Given
        when(postRepository.countByUserId(anyLong())).thenReturn(5);

        // When
        int count = postService.getPostsNrOfUser(user.getId());

        // Then
        assertEquals(5, count);
        verify(postRepository, times(1)).countByUserId(user.getId());
    }

    @Test
    public void testCreatePost() throws IOException {
        // Given
        String jwt = "valid.jwt.token";
        String text = "Sample post content";
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());

        // Mock S3Service
        when(s3Service.generatePostKey(anyLong(), anyLong(), any())).thenReturn("/file/path");
        doNothing().when(s3Service).putObject(anyString(), any(byte[].class));

        // When
        Post createdPost = postService.createPost(jwt, file, text);

        // Then
        assertNotNull(createdPost);
        assertEquals(post.getId(), createdPost.getId());
        assertEquals(content.getId(), createdPost.getContent().getId());
        verify(postRepository, times(2)).save(any(Post.class));
        verify(contentRepository, times(1)).save(any(Content.class));
        verify(s3Service, times(1)).putObject(anyString(), any(byte[].class));
    }

    @Test
    public void testDeletePost() throws Exception {
        // Given
        String jwt = "valid.jwt.token";

        // Mock ModeratorService
        when(moderatorService.isModerator(anyString())).thenReturn(true);

        // When
        postService.deletePost(post.getId(), jwt);

        // Then
        verify(commentRepository, times(1)).deleteByPostId(post.getId());
        verify(likeRepository, times(1)).deleteByPostId(post.getId());
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    public void testDeletePost_Unauthorized() {
        // Given
        String jwt = "valid.jwt.token";
        User anotherUser = new User("jane_doe", "password456", "jane_doe@example.com", "/profile/path2");
        anotherUser.setId(2L);
        post.setUser(anotherUser);

        // Mock UserService and ModeratorService
        when(userService.findUserByJwt(anyString())).thenReturn(user);
        when(moderatorService.isModerator(anyString())).thenReturn(false);

        // When / Then
        Exception exception = assertThrows(IllegalAccessException.class, () -> {
            postService.deletePost(post.getId(), jwt);
        });

        assertEquals("Unauthorized to delete this post", exception.getMessage());
        verify(postRepository, times(0)).delete(post);
    }

    @Test
    public void testGetAllPostsByUser() {
        // Given
        when(postRepository.findByUser_Id(anyLong())).thenReturn(Collections.singletonList(post));

        // When
        List<Post> posts = postService.getAllPostsByUser(user.getId());

        // Then
        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals(post.getId(), posts.get(0).getId());
        verify(postRepository, times(1)).findByUser_Id(user.getId());
    }

    @Test
    public void testGetLikesCountForPost() {
        // Given
        when(likeRepository.countByPostId(anyLong())).thenReturn(10L);

        // When
        long count = postService.getLikesCountForPost(post.getId());

        // Then
        assertEquals(10L, count);
        verify(likeRepository, times(1)).countByPostId(post.getId());
    }

    @Test
    public void testGetUsersWhoLikedPost() {
        // Given
        User anotherUser = new User("jane_doe", "password456", "jane_doe@example.com", "/profile/path2");
        anotherUser.setId(2L);
        when(likeRepository.findUsersByPostId(anyLong())).thenReturn(Collections.singletonList(anotherUser));

        // When
        List<UserResponse> usersWhoLikedPost = postService.getUsersWhoLikedPost(post.getId());

        // Then
        assertNotNull(usersWhoLikedPost);
        assertEquals(1, usersWhoLikedPost.size());
        assertEquals("jane_doe", usersWhoLikedPost.get(0).getUsername());
        verify(likeRepository, times(1)).findUsersByPostId(post.getId());
    }

    @Test
    public void testGetPostMedia() throws Exception {
        // Given
        when(s3Service.getObject(anyString())).thenReturn("some media content".getBytes());

        // When
        byte[] media = postService.getPostMedia(post.getId());

        // Then
        assertNotNull(media);
        assertEquals("some media content", new String(media));
        verify(postRepository, times(1)).findById(post.getId());
        verify(s3Service, times(1)).getObject(post.getContent().getFilePath());
    }

    @Test
    public void testGetPostsByFriends() {
        // Given
        String jwt = "valid.jwt.token";
        when(postRepository.findAllPostsByFriends(anyLong())).thenReturn(Collections.singletonList(post));

        // When
        List<Post> posts = postService.getPostsByFriends(jwt);

        // Then
        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals(post.getId(), posts.get(0).getId());
        verify(postRepository, times(1)).findAllPostsByFriends(user.getId());
    }

    @Test
    public void testUpdatePostContent() throws Exception {
        // Given
        String jwt = "valid.jwt.token";
        String newContent = "Updated content";
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());

        // Mock S3Service
        when(s3Service.generatePostKey(anyLong(), anyLong(), any())).thenReturn("/new/file/path");
        doNothing().when(s3Service).putObject(anyString(), any(byte[].class));

        // When
        Post updatedPost = postService.updatePostContent(post.getId(), newContent, file, jwt);

        // Then
        assertNotNull(updatedPost);
        assertEquals(newContent, updatedPost.getContent().getTextContent());
        assertEquals("/new/file/path", updatedPost.getContent().getFilePath());
        verify(contentRepository, times(1)).save(any(Content.class));
        verify(postRepository, times(1)).save(post);
        verify(s3Service, times(1)).putObject(anyString(), any(byte[].class));
    }

    @Test
    public void testGetPostsOrderedByReportCount() {
        // Given
        when(postRepository.findAllPostsOrderedByReportCount()).thenReturn(Collections.singletonList(post));

        // When
        List<Post> posts = postService.getPostsOrderedByReportCount();

        // Then
        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals(post.getId(), posts.get(0).getId());
        verify(postRepository, times(1)).findAllPostsOrderedByReportCount();
    }
}
