package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Model.FriendsListId;
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
public class FriendsListRepositoryTests {

    @Autowired
    private FriendsListRepository friendsListRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {
        // Initialize and save Users
        user1 = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user1 = userRepository.save(user1);

        user2 = new User("jane_doe", "password456", "jane_doe@example.com", "/profile/path2");
        user2 = userRepository.save(user2);

        user3 = new User("alice_smith", "password789", "alice_smith@example.com", "/profile/path3");
        user3 = userRepository.save(user3);

        // Initialize and save FriendsList
        FriendsListId friendsListId1 = new FriendsListId(user1, user2);
        FriendsList friendsList1 = new FriendsList();
        friendsList1.setId(friendsListId1);
        friendsListRepository.save(friendsList1);

        FriendsListId friendsListId2 = new FriendsListId(user1, user3);
        FriendsList friendsList2 = new FriendsList();
        friendsList2.setId(friendsListId2);
        friendsListRepository.save(friendsList2);
    }

    @Test
    public void testCountNrOfFriends() {
        // When
        int count = friendsListRepository.countNrOfFriends(user1.getId());

        // Then
        assertEquals(2, count);
    }

    @Test
    public void testIsFriendshipExists() {
        // When
        boolean exists = friendsListRepository.isFriendshipExists(user1.getId(), user2.getId());

        // Then
        assertTrue(exists);
    }

    @Test
    public void testFindFriendsByUserId() {
        // When
        List<User> friends = friendsListRepository.findFriendsByUserId(user1.getId());

        // Then
        assertNotNull(friends);
        assertEquals(2, friends.size());
        assertTrue(friends.contains(user2));
        assertTrue(friends.contains(user3));
    }

    @Test
    public void testFindByUsers() {
        // When
        Optional<FriendsList> friendsList = friendsListRepository.findByUsers(user1.getId(), user2.getId());

        // Then
        assertTrue(friendsList.isPresent());
        assertEquals(user1.getId(), friendsList.get().getId().getUser1().getId());
        assertEquals(user2.getId(), friendsList.get().getId().getUser2().getId());
    }
}