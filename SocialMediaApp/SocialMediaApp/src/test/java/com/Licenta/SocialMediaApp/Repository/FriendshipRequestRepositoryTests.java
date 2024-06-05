package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.FriendshipRequest;
import com.Licenta.SocialMediaApp.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class FriendshipRequestRepositoryTests {

    @Autowired
    private FriendshipRequestRepository friendshipRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User sender;
    private User receiver;

    @BeforeEach
    public void setUp() {
        // Initialize and save Users
        sender = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        sender = userRepository.save(sender);

        receiver = new User("jane_doe", "password456", "jane_doe@example.com", "/profile/path2");
        receiver = userRepository.save(receiver);

        // Initialize and save FriendshipRequest
        FriendshipRequest request = new FriendshipRequest(sender, receiver, "PENDING");
        friendshipRequestRepository.save(request);
    }

    @Test
    public void testFindBySenderIdAndReceiverId() {
        // When
        Optional<FriendshipRequest> request = friendshipRequestRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId());

        // Then
        assertTrue(request.isPresent());
        assertEquals("PENDING", request.get().getStatus());
    }

    @Test
    public void testFindSendersByReceiverIdWithPendingStatus() {
        // When
        List<User> senders = friendshipRequestRepository.findSendersByReceiverIdWithPendingStatus(receiver.getId());

        // Then
        assertNotNull(senders);
        assertEquals(1, senders.size());
        assertEquals(sender.getId(), senders.get(0).getId());
    }

    @Test
    public void testCountPendingFriendshipRequests() {
        // When
        int count = friendshipRequestRepository.countPendingFriendshipRequests(receiver.getId());

        // Then
        assertEquals(1, count);
    }
}
