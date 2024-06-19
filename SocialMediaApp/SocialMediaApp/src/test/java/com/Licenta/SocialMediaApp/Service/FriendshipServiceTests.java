package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Model.FriendsListId;
import com.Licenta.SocialMediaApp.Model.FriendshipRequest;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.FriendsListRepository;
import com.Licenta.SocialMediaApp.Repository.FriendshipRequestRepository;
import com.Licenta.SocialMediaApp.Service.ServiceImpl.FriendshipRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTests {

    @Mock
    private FriendshipRequestRepository friendshipRequestRepository;

    @Mock
    private FriendsListRepository friendsListRepository;

    @Mock
    private FriendsListService friendsListService;

    @Mock
    private UserService userService;

    @InjectMocks
    private FriendshipRequestServiceImpl friendshipService;

    private User user;
    private User sender;
    private FriendshipRequest friendshipRequest;
    private FriendsList friendsList;

    @BeforeEach
    public void setUp() {
        // Initialize Users
        user = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user.setId(1L);
        sender = new User("jane_doe", "password123", "jane_doe@example.com", "/profile/path2");
        sender.setId(2L);

        // Initialize FriendshipRequest
        friendshipRequest = new FriendshipRequest(sender, user, "PENDING");
        friendshipRequest.setId(1L);

        // Initialize FriendsList
        FriendsListId friendsListId = new FriendsListId(user, sender);
        friendsList = new FriendsList();
        friendsList.setId(friendsListId);
    }

    @Test
    public void testFindFriendshipRequestsSenders() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(friendshipRequestRepository.findSendersByReceiverIdWithPendingStatus(anyLong())).thenReturn(List.of(sender));

        // When
        List<UserResponse> result = friendshipService.findFriendshipRequestsSenders(jwt);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sender.getUsername(), result.get(0).getUsername());
        verify(friendshipRequestRepository, times(1)).findSendersByReceiverIdWithPendingStatus(user.getId());
    }

    @Test
    public void testGetNrOfFrienshipRequests() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(friendshipRequestRepository.countPendingFriendshipRequests(anyLong())).thenReturn(3);

        // When
        int count = friendshipService.getNrOfFrienshipRequests(jwt);

        // Then
        assertEquals(3, count);
        verify(friendshipRequestRepository, times(1)).countPendingFriendshipRequests(user.getId());
    }

    @Test
    public void testDeclineFriendshipRequest() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(friendshipRequestRepository.findBySenderIdAndReceiverId(sender.getId(), user.getId())).thenReturn(Optional.of(friendshipRequest));

        // When
        friendshipService.declineFriendshipRequest(jwt, sender.getId());

        // Then
        assertEquals("DECLINED", friendshipRequest.getStatus());
        verify(friendshipRequestRepository, times(1)).save(friendshipRequest);
    }

    @Test
    public void testDeclineFriendshipRequest_RequestNotFound() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(friendshipRequestRepository.findBySenderIdAndReceiverId(sender.getId(), user.getId())).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            friendshipService.declineFriendshipRequest(jwt, sender.getId());
        });

        assertEquals("Request not found", exception.getMessage());
    }

    @Test
    public void testAcceptFriendshipRequest() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(friendshipRequestRepository.findBySenderIdAndReceiverId(sender.getId(), user.getId())).thenReturn(Optional.of(friendshipRequest));
        when(friendsListService.createFriendsList(any(FriendsList.class))).thenReturn(friendsList);

        // When
        friendshipService.acceptFriendshipRequest(jwt, sender.getId());

        // Then
        assertEquals("ACCEPTED", friendshipRequest.getStatus());
        verify(friendshipRequestRepository, times(1)).save(friendshipRequest);
        verify(friendsListService, times(1)).createFriendsList(any(FriendsList.class));
    }

    @Test
    public void testAcceptFriendshipRequest_RequestNotFound() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(user);
        when(friendshipRequestRepository.findBySenderIdAndReceiverId(sender.getId(), user.getId())).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            friendshipService.acceptFriendshipRequest(jwt, sender.getId());
        });

        assertEquals("Request not found", exception.getMessage());
    }
}
