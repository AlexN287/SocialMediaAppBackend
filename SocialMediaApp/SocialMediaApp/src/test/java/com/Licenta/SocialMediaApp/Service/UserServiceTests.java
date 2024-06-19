package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Config.Security.JwtProvider;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.FriendsListRepository;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.ServiceImpl.UserServiceImpl;
import com.Licenta.SocialMediaApp.Service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private S3Service s3Service;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Cache<Long, byte[]> profileImageCache;

    @Mock
    private FriendsListRepository friendsListRepository;

    @Mock
    private SimpUserRegistry simpUserRegistry;

    @BeforeEach
    public void setUp() {
        // Initialize mocks and service if needed
    }

    /*@Test
    public void testFindByUsernameContainingIgnoreCase() {
        // Given
        User user1 = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");
        User user2 = new User("jane_doe", "password2", "jane_doe@example.com", "profileImagePath2");
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findByUsernameContainingIgnoreCase("doe")).thenReturn(users);

        // When
        List<User> foundUsers = userService.findByUsernameContainingIgnoreCase("doe");

        // Then
        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(user1));
        assertTrue(foundUsers.contains(user2));
        verify(userRepository, times(1)).findByUsernameContainingIgnoreCase("doe");
    }*/

    @Test
    public void testFindUserByJwt() {
        // Given
        String jwt = "valid.jwt.token"; // This is a mock JWT with a correct format
        String username = "john_doe";
        User expectedUser = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");

        // Mock the static method JwtProvider.getUsernameFromJwtToken
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.getUsernameFromJwtToken(jwt)).thenReturn(username);
            when(userRepository.getUsersByUsername(username)).thenReturn(expectedUser);

            // When
            User actualUser = userService.findUserByJwt(jwt);

            // Then
            assertNotNull(actualUser);
            assertEquals(expectedUser, actualUser);
            mockedJwtProvider.verify(() -> JwtProvider.getUsernameFromJwtToken(jwt), times(1));
            verify(userRepository, times(1)).getUsersByUsername(username);
        }
    }

    @Test
    public void testUpdateUsername() throws Exception {
        // Given
        String jwt = "valid.jwt.token"; // This is a mock JWT with a correct format
        String newUsername = "new_username";
        String currentUsername = "current_username";
        String newJwt = "new.jwt.token";

        User user = new User(currentUsername, "password", "user@example.com", "profileImagePath");

        // Mock the behavior of JwtProvider and UserRepository
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.getUsernameFromJwtToken(jwt)).thenReturn(currentUsername);
            mockedJwtProvider.when(() -> JwtProvider.generateToken(newUsername)).thenReturn(newJwt);

            when(userRepository.existsByUsername(newUsername)).thenReturn(false);
            when(userRepository.getUsersByUsername(currentUsername)).thenReturn(user);
            when(userRepository.save(user)).thenReturn(user);

            // When
            String resultJwt = userService.updateUsername(newUsername, jwt);

            // Then
            assertNotNull(resultJwt);
            assertEquals(newJwt, resultJwt);
            assertEquals(newUsername, user.getUsername());
            verify(userRepository, times(1)).existsByUsername(newUsername);
            verify(userRepository, times(1)).save(user);
            mockedJwtProvider.verify(() -> JwtProvider.getUsernameFromJwtToken(jwt), times(1));
            mockedJwtProvider.verify(() -> JwtProvider.generateToken(newUsername), times(1));
        }
    }

    @Test
    public void testUpdateUsernameThrowsExceptionIfUsernameExists() {
        // Given
        String jwt = "valid.jwt.token";
        String newUsername = "new_username";

        // Mock the behavior of UserRepository
        when(userRepository.existsByUsername(newUsername)).thenReturn(true);

        // When / Then
        Exception exception = assertThrows(Exception.class, () -> {
            userService.updateUsername(newUsername, jwt);
        });

        assertEquals("Username already exists.", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(newUsername);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testExistsByUsername_UsernameExists() {
        // Given
        String username = "existing_username";
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When
        boolean exists = userService.existsByUsername(username);

        // Then
        assertTrue(exists);
        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    public void testExistsByUsername_UsernameDoesNotExist() {
        // Given
        String username = "non_existing_username";
        when(userRepository.existsByUsername(username)).thenReturn(false);

        // When
        boolean exists = userService.existsByUsername(username);

        // Then
        assertFalse(exists);
        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    public void testChangePassword_Success() {
        // Given
        String jwt = "valid.jwt.token";
        String oldPassword = "old_password";
        String newPassword = "new_password";
        String username = "john_doe";
        User user = new User(username, "encoded_old_password", "john_doe@example.com", "profileImagePath");

        // Mock the behavior of JwtProvider and UserRepository
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.getUsernameFromJwtToken(jwt)).thenReturn(username);
            when(userRepository.getUsersByUsername(username)).thenReturn(user);
            when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
            when(passwordEncoder.encode(newPassword)).thenReturn("encoded_new_password");

            // When
            userService.changePassword(oldPassword, newPassword, jwt);

            // Then
            assertEquals("encoded_new_password", user.getPassword());
            verify(userRepository, times(1)).save(user);
            mockedJwtProvider.verify(() -> JwtProvider.getUsernameFromJwtToken(jwt), times(1));
            verify(passwordEncoder, times(1)).matches(oldPassword, "encoded_old_password");
            verify(passwordEncoder, times(1)).encode(newPassword);
        }
    }

    @Test
    public void testChangePassword_IncorrectOldPassword() {
        // Given
        String jwt = "valid.jwt.token";
        String oldPassword = "incorrect_old_password";
        String newPassword = "new_password";
        String username = "john_doe";
        User user = new User(username, "encoded_old_password", "john_doe@example.com", "profileImagePath");

        // Mock the behavior of JwtProvider and UserRepository
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.getUsernameFromJwtToken(jwt)).thenReturn(username);
            when(userRepository.getUsersByUsername(username)).thenReturn(user);
            when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(false);

            // When / Then
            Exception exception = assertThrows(BadCredentialsException.class, () -> {
                userService.changePassword(oldPassword, newPassword, jwt);
            });

            assertEquals("Password incorrect", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
            mockedJwtProvider.verify(() -> JwtProvider.getUsernameFromJwtToken(jwt), times(1));
            verify(passwordEncoder, times(1)).matches(oldPassword, user.getPassword());
            verify(passwordEncoder, never()).encode(newPassword);
        }
    }

    @Test
    public void testFindById_UserExists() {
        // Given
        Long userId = 1L;
        User expectedUser = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // When
        User actualUser = userService.findById(userId);

        // Then
        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testFindById_UserDoesNotExist() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.findById(userId);
        });

        assertEquals("User not found with ID: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testUploadUserProfileImage_UserExists() throws Exception {
        // Given
        Long userId = 1L;
        User user = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test image content".getBytes(StandardCharsets.UTF_8));
        String profileImageKey = "user-profile-image-key";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.generateProfileImageKey(userId, file)).thenReturn(profileImageKey);

        // When
        userService.uploadUserProfileImage(userId, file);

        // Then
        assertEquals(profileImageKey, user.getProfileImagePath());
        verify(userRepository, times(1)).findById(userId);
        verify(s3Service, times(1)).generateProfileImageKey(userId, file);
        verify(s3Service, times(1)).putObject(profileImageKey, file.getBytes());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUploadUserProfileImage_UserDoesNotExist() {
        // Given
        Long userId = 1L;
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test image content".getBytes(StandardCharsets.UTF_8));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        Exception exception = assertThrows(Exception.class, () -> {
            userService.uploadUserProfileImage(userId, file);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(s3Service, never()).generateProfileImageKey(anyLong(), any(MultipartFile.class));
        verify(s3Service, never()).putObject(anyString(), any(byte[].class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testGetUserProfileImage_UserExistsAndImageCached() throws Exception {
        // Given
        Long userId = 1L;
        String jwt = "valid.jwt.token";
        User user = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");
        byte[] cachedImage = "cached image content".getBytes();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(profileImageCache.getIfPresent(userId)).thenReturn(cachedImage);

        // When
        byte[] actualImage = userService.getUserProfileImage(userId, jwt);

        // Then
        assertArrayEquals(cachedImage, actualImage);
        verify(userRepository, times(1)).findById(userId);
        verify(profileImageCache, times(1)).getIfPresent(userId);
        verifyNoInteractions(s3Service);
    }

    @Test
    public void testGetUserProfileImage_UserExistsAndImageNotCached() throws Exception {
        // Given
        Long userId = 1L;
        String jwt = "valid.jwt.token";
        User user = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");
        byte[] imageBytes = "image content".getBytes();
        User loggedUser = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");
        loggedUser.setId(0L); // Ensuring loggedUser has a different ID for the test

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(profileImageCache.getIfPresent(userId)).thenReturn(null);
        when(s3Service.getObject(user.getProfileImagePath())).thenReturn(imageBytes);

        // Mock JwtProvider to return the logged user's username
        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.getUsernameFromJwtToken(jwt)).thenReturn("john_doe");
            when(userRepository.getUsersByUsername("john_doe")).thenReturn(loggedUser);

            when(friendsListRepository.isFriendshipExists(0L, userId)).thenReturn(true);

            // When
            byte[] actualImage = userService.getUserProfileImage(userId, jwt);

            // Then
            assertArrayEquals(imageBytes, actualImage);
            verify(userRepository, times(1)).findById(userId);
            verify(profileImageCache, times(1)).getIfPresent(userId);
            verify(s3Service, times(1)).getObject(user.getProfileImagePath());
            verify(profileImageCache, times(1)).put(userId, imageBytes);
            verify(friendsListRepository, times(1)).isFriendshipExists(0L, userId);
        }
    }

    @Test
    public void testGetUserProfileImage_UserDoesNotExist() {
        // Given
        Long userId = 1L;
        String jwt = "valid.jwt.token";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        Exception exception = assertThrows(Exception.class, () -> {
            userService.getUserProfileImage(userId, jwt);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(profileImageCache);
        verifyNoInteractions(s3Service);
    }

    @Test
    public void testGetConnectedFriends() {
        // Given
        String jwt = "valid.jwt.token";
        User loggedUser = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");
        loggedUser.setId(1L);
        List<User> friends = Arrays.asList(
                new User("friend1", "password2", "friend1@example.com", "profileImagePath2"),
                new User("friend2", "password3", "friend2@example.com", "profileImagePath3")
        );

        SimpUser simpUser1 = mock(SimpUser.class);
        SimpUser simpUser2 = mock(SimpUser.class);
        when(simpUser1.getName()).thenReturn("friend1");
        when(simpUser2.getName()).thenReturn("friend2");
        when(simpUserRegistry.getUsers()).thenReturn(Stream.of(simpUser1, simpUser2).collect(Collectors.toSet()));

        try (MockedStatic<JwtProvider> mockedJwtProvider = mockStatic(JwtProvider.class)) {
            mockedJwtProvider.when(() -> JwtProvider.getUsernameFromJwtToken(jwt)).thenReturn("john_doe");
            when(userRepository.getUsersByUsername("john_doe")).thenReturn(loggedUser);
            when(friendsListRepository.findFriendsByUserId(loggedUser.getId())).thenReturn(friends);

            // When
            List<UserResponse> connectedFriends = userService.getConnectedFriends(jwt);

            // Then
            assertNotNull(connectedFriends);
            assertEquals(2, connectedFriends.size());
            assertTrue(connectedFriends.stream().anyMatch(user -> user.getUsername().equals("friend1")));
            assertTrue(connectedFriends.stream().anyMatch(user -> user.getUsername().equals("friend2")));
            verify(friendsListRepository, times(1)).findFriendsByUserId(loggedUser.getId());
            verify(simpUserRegistry, times(1)).getUsers();
        }
    }
}
