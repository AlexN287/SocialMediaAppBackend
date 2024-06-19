package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.Like;
import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.LikeRepository;
import com.Licenta.SocialMediaApp.Repository.PostRepository;
import com.Licenta.SocialMediaApp.Service.ServiceImpl.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTests {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    private User user;
    private Post post;
    private Like like;

    @BeforeEach
    public void setUp() {
        // Initialize User
        user = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user.setId(1L);

        // Initialize Post
        post = new Post();
        post.setId(1L);

        // Initialize Like
        like = new Like();
        like.setId(1L);
        like.setUser(user);
        like.setPost(post);
    }

    @Test
    public void testAddLike_UserNotFound() {
        // Given
        String jwt = "invalid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(null);

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            likeService.addLike(jwt, post.getId());
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testAddLike_PostNotFound() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            likeService.addLike(jwt, post.getId());
        });

        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    public void testAddLike_AlreadyLiked() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(likeRepository.existsByUserAndPost(any(User.class), any(Post.class))).thenReturn(true);

        // When / Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            likeService.addLike(jwt, post.getId());
        });

        assertEquals("User already liked this post", exception.getMessage());
    }

    @Test
    public void testAddLike_Success() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(likeRepository.existsByUserAndPost(any(User.class), any(Post.class))).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        // When
        Like savedLike = likeService.addLike(jwt, post.getId());

        // Then
        assertNotNull(savedLike);
        assertEquals(like.getId(), savedLike.getId());
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    public void testDeleteLike_UserNotFound() {
        // Given
        String jwt = "invalid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(null);

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            likeService.deleteLike(jwt, post.getId());
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testDeleteLike_PostNotFound() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            likeService.deleteLike(jwt, post.getId());
        });

        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    public void testDeleteLike_LikeNotFound() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(any(User.class), any(Post.class))).thenReturn(null);

        // When / Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            likeService.deleteLike(jwt, post.getId());
        });

        assertEquals("Like not found", exception.getMessage());
    }

    @Test
    public void testDeleteLike_Success() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(any(User.class), any(Post.class))).thenReturn(like);

        // When
        likeService.deleteLike(jwt, post.getId());

        // Then
        verify(likeRepository, times(1)).delete(any(Like.class));
    }

    @Test
    public void testCheckUserLikedPost_UserNotFound() {
        // Given
        String jwt = "invalid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(null);

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            likeService.checkUserLikedPost(jwt, post.getId());
        });

        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    public void testCheckUserLikedPost_PostNotFound() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            likeService.checkUserLikedPost(jwt, post.getId());
        });

        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    public void testCheckUserLikedPost_Success() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(likeRepository.existsByUserAndPost(any(User.class), any(Post.class))).thenReturn(true);

        // When
        boolean result = likeService.checkUserLikedPost(jwt, post.getId());

        // Then
        assertTrue(result);
        verify(likeRepository, times(1)).existsByUserAndPost(any(User.class), any(Post.class));
    }
}