package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Model.FriendsListId;
import com.Licenta.SocialMediaApp.Model.FriendshipRequest;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.FriendsListRepository;
import com.Licenta.SocialMediaApp.Repository.FriendshipRequestRepository;
import com.Licenta.SocialMediaApp.Service.ServiceImpl.FriendsListServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendsListServiceTests {

    @Mock
    private FriendsListRepository friendsListRepository;

    @Mock
    private FriendshipRequestRepository friendshipRequestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FriendsListServiceImpl friendsListService;

    private User user1;
    private User user2;
    private FriendsList friendsList;
    private FriendshipRequest friendshipRequest;

    @BeforeEach
    public void setUp() {
        // Initialize Users
        user1 = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user1.setId(1L);
        user2 = new User("jane_doe", "password123", "jane_doe@example.com", "/profile/path2");
        user2.setId(2L);

        // Initialize FriendsList
        FriendsListId friendsListId = new FriendsListId(user1, user2);
        friendsList = new FriendsList();
        friendsList.setId(friendsListId);

        // Initialize FriendshipRequest
        friendshipRequest = new FriendshipRequest(user1, user2, "PENDING");
        friendshipRequest.setId(1L);
    }

    @Test
    public void testCountNrOfFriends() {
        // Given
        when(friendsListRepository.countNrOfFriends(anyLong())).thenReturn(5);

        // When
        int count = friendsListService.countNrOfFriends(user1.getId());

        // Then
        assertEquals(5, count);
        verify(friendsListRepository, times(1)).countNrOfFriends(user1.getId());
    }

    @Test
    public void testIsFriendshipExists() {
        // Given
        when(friendsListRepository.isFriendshipExists(anyLong(), anyLong())).thenReturn(true);

        // When
        boolean exists = friendsListService.isFriendshipExists(user1.getId(), user2.getId());

        // Then
        assertTrue(exists);
        verify(friendsListRepository, times(1)).isFriendshipExists(user1.getId(), user2.getId());
    }

    @Test
    public void testFindFriendsByUserId() {
        // Given
        List<User> friends = Arrays.asList(user2);
        when(friendsListRepository.findFriendsByUserId(anyLong())).thenReturn(friends);

        // When
        List<User> result = friendsListService.findFriendsByUserId(user1.getId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getPassword());
        assertNull(result.get(0).getProfileImagePath());
        verify(friendsListRepository, times(1)).findFriendsByUserId(user1.getId());
    }

    @Test
    public void testCreateFriendsList() {
        // Given
        when(friendsListRepository.save(any(FriendsList.class))).thenReturn(friendsList);

        // When
        FriendsList savedFriendsList = friendsListService.createFriendsList(friendsList);

        // Then
        assertNotNull(savedFriendsList);
        assertEquals(friendsList.getId(), savedFriendsList.getId());
        verify(friendsListRepository, times(1)).save(friendsList);
    }

    @Test
    public void testDeleteFriend() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user1);
        when(friendshipRequestRepository.findBySenderIdAndReceiverId(user1.getId(), user2.getId())).thenReturn(Optional.of(friendshipRequest));
        when(friendsListRepository.findByUsers(user1.getId(), user2.getId())).thenReturn(Optional.of(friendsList));

        // When
        friendsListService.deleteFriend(jwt, user2.getId());

        // Then
        verify(friendshipRequestRepository, times(1)).delete(friendshipRequest);
        verify(friendsListRepository, times(1)).delete(friendsList);
    }

    @Test
    public void testDeleteFriend_FriendshipRequestNotFound() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user1);
        when(friendshipRequestRepository.findBySenderIdAndReceiverId(user1.getId(), user2.getId())).thenReturn(Optional.empty());
        when(friendshipRequestRepository.findBySenderIdAndReceiverId(user2.getId(), user1.getId())).thenReturn(Optional.of(friendshipRequest));
        when(friendsListRepository.findByUsers(user1.getId(), user2.getId())).thenReturn(Optional.of(friendsList));

        // When
        friendsListService.deleteFriend(jwt, user2.getId());

        // Then
        verify(friendshipRequestRepository, times(1)).delete(friendshipRequest);
        verify(friendsListRepository, times(1)).delete(friendsList);
    }

    @Test
    public void testDeleteFriend_FriendListNotFound() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user1);
        when(friendshipRequestRepository.findBySenderIdAndReceiverId(user1.getId(), user2.getId())).thenReturn(Optional.of(friendshipRequest));
        when(friendsListRepository.findByUsers(user1.getId(), user2.getId())).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            friendsListService.deleteFriend(jwt, user2.getId());
        });

        assertEquals("FriendList not found", exception.getMessage());
        verify(friendshipRequestRepository, times(1)).delete(friendshipRequest);
    }
}