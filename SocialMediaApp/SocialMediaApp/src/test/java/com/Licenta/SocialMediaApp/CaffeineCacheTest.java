package com.Licenta.SocialMediaApp;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Config.Security.JwtProvider;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.FriendsListRepository;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.ServiceImpl.UserServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CaffeineCacheTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private FriendsListRepository friendsListRepository;

    @Mock
    private Cache<Long, byte[]> testProfileImageCache;

    @InjectMocks
    private UserServiceImpl userService; // Using UserServiceImpl

    @BeforeEach
    public void setUp() {
        testProfileImageCache.invalidateAll(); // Clear the cache before each test
    }

    @Test
    public void testCacheEviction() throws InterruptedException {
        // Given
        Long userId = 1L;
        byte[] imageData = "test image data".getBytes();

        // When
        testProfileImageCache.put(userId, imageData);

        // Then
        assertNotNull(testProfileImageCache.getIfPresent(userId));
        Thread.sleep(11000); // Sleep for longer than the cache expiration time (10 seconds)
        assertNull(testProfileImageCache.getIfPresent(userId));
    }

    @Test
    public void testCacheEvictionAfterMaxSize() {
        // Given
        for (long i = 1; i <= 10; i++) {
            testProfileImageCache.put(i, ("test image data " + i).getBytes());
        }

        // Ensure cache is filled to maximum size
        assertEquals(10, testProfileImageCache.asMap().size(), "Cache should have 10 entries");

        // When adding one more entry beyond the maximum size
        testProfileImageCache.put(11L, "test image data 11".getBytes());

        // Allow some time for the eviction to potentially occur
        try {
            Thread.sleep(100); // Short sleep to allow any potential asynchronous eviction
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Then
        assertTrue(testProfileImageCache.asMap().size() <= 10, "Cache should have at most 10 entries after exceeding max size");

        // Ensure that the first entry has been evicted
        assertNull(testProfileImageCache.getIfPresent(1L), "Entry with key 1 should have been evicted");
        assertNotNull(testProfileImageCache.getIfPresent(11L), "Entry with key 11 should be present");

        // Ensure that the remaining entries are in the cache
        for (long i = 2; i <= 11; i++) {
            if (i != 1L) { // Skip the entry 1L as it should be evicted
                assertNotNull(testProfileImageCache.getIfPresent(i), "Entry with key " + i + " should be present");
            }
        }
    }

    @Test
    public void testGetUserProfileImage_StoresLoggedUserImage() throws Exception {
        // Given
        Long userId = 1L;
        String jwt = "valid.jwt.token";
        User user = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");
        user.setId(userId);
        byte[] imageBytes = "test image data".getBytes();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.getObject(user.getProfileImagePath())).thenReturn(imageBytes);

        // Mock JwtProvider to return the logged user's username
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.getUsernameFromJwtToken(jwt)).thenReturn("john_doe");
            when(userRepository.getUsersByUsername("john_doe")).thenReturn(user);

            // When
            byte[] result = userService.getUserProfileImage(userId, jwt);

            // Then
            assertArrayEquals(imageBytes, result);
            verify(testProfileImageCache, times(1)).put(userId, imageBytes);
        }
    }

    @Test
    public void testGetUserProfileImage_StoresFriendImage() throws Exception {
        // Given
        Long userId = 2L;
        Long loggedUserId = 1L;
        String jwt = "valid.jwt.token";
        User user = new User("friend_doe", "password2", "friend_doe@example.com", "profileImagePath2");
        user.setId(userId);
        User loggedUser = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");
        loggedUser.setId(loggedUserId);
        byte[] imageBytes = "test image data".getBytes();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.getObject(user.getProfileImagePath())).thenReturn(imageBytes);
        when(friendsListRepository.isFriendshipExists(loggedUserId, userId)).thenReturn(true);

        // Mock JwtProvider to return the logged user's username
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.getUsernameFromJwtToken(jwt)).thenReturn("john_doe");
            when(userRepository.getUsersByUsername("john_doe")).thenReturn(loggedUser);

            // When
            byte[] result = userService.getUserProfileImage(userId, jwt);

            // Then
            assertArrayEquals(imageBytes, result);
            verify(testProfileImageCache, times(1)).put(userId, imageBytes);
        }
    }

    @Test
    public void testGetUserProfileImage_DoesNotStoreNonFriendImage() throws Exception {
        // Given
        Long userId = 2L;
        Long loggedUserId = 1L;
        String jwt = "valid.jwt.token";
        User user = new User("non_friend_doe", "password2", "non_friend_doe@example.com", "profileImagePath2");
        user.setId(userId);
        User loggedUser = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");
        loggedUser.setId(loggedUserId);
        byte[] imageBytes = "test image data".getBytes();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.getObject(user.getProfileImagePath())).thenReturn(imageBytes);
        when(friendsListRepository.isFriendshipExists(loggedUserId, userId)).thenReturn(false);

        // Mock JwtProvider to return the logged user's username
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.getUsernameFromJwtToken(jwt)).thenReturn("john_doe");
            when(userRepository.getUsersByUsername("john_doe")).thenReturn(loggedUser);

            // When
            byte[] result = userService.getUserProfileImage(userId, jwt);

            // Then
            assertArrayEquals(imageBytes, result);
            verify(testProfileImageCache, never()).put(userId, imageBytes);
        }
    }
}
