package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ConversationMembersRepositoryTests {
    @Autowired
    private ConversationMembersRepository conversationMembersRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsListRepository friendsListRepository;

    private User user1;
    private User user2;
    private User user3;
    private Conversation conversation1;
    private Conversation conversation2;

    @BeforeEach
    public void setUp() {
        // Initialize and save Users
        user1 = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user1 = userRepository.save(user1);

        user2 = new User("jane_doe", "password456", "jane_doe@example.com", "/profile/path2");
        user2 = userRepository.save(user2);

        user3 = new User("alice_smith", "password789", "alice_smith@example.com", "/profile/path3");
        user3 = userRepository.save(user3);

        // Initialize and save Conversations
        conversation1 = new Conversation();
        conversation1.setName("Conversation 1");
        conversation1 = conversationRepository.save(conversation1);

        conversation2 = new Conversation();
        conversation2.setName("Conversation 2");
        conversation2 = conversationRepository.save(conversation2);

        // Initialize and save ConversationMembers
        ConversationMembers cm1 = new ConversationMembers(new ConversationMembersId(conversation1, user1));
        conversationMembersRepository.save(cm1);

        ConversationMembers cm2 = new ConversationMembers(new ConversationMembersId(conversation1, user2));
        conversationMembersRepository.save(cm2);

        ConversationMembers cm3 = new ConversationMembers(new ConversationMembersId(conversation2, user1));
        conversationMembersRepository.save(cm3);

        ConversationMembers cm4 = new ConversationMembers(new ConversationMembersId(conversation2, user3));
        conversationMembersRepository.save(cm4);

        // Initialize and save FriendsList
        FriendsList friendsList1 = new FriendsList(new FriendsListId(user1, user2));
        friendsListRepository.save(friendsList1);

        FriendsList friendsList2 = new FriendsList(new FriendsListId(user1, user3));
        friendsListRepository.save(friendsList2);
    }

    @Test
    public void testFindOtherUserInPrivateConversation() {
        // When
        User otherUser = conversationMembersRepository.findOtherUserInPrivateConversation(conversation1.getId(), user1.getId());

        // Then
        assertNotNull(otherUser);
        assertEquals(user2.getId(), otherUser.getId());
    }

    @Test
    public void testExistsById() {
        // Given
        ConversationMembersId cmId = new ConversationMembersId(conversation1, user1);

        // When
        boolean exists = conversationMembersRepository.existsById(cmId);

        // Then
        assertTrue(exists);
    }

    @Test
    public void testDeleteById() {
        // Given
        ConversationMembersId cmId = new ConversationMembersId(conversation1, user1);

        // When
        conversationMembersRepository.deleteById(cmId);

        // Then
        boolean exists = conversationMembersRepository.existsById(cmId);
        assertFalse(exists);
    }

    @Test
    public void testFindConversationIdByUserIds() {
        // When
        List<Integer> conversationIds = conversationMembersRepository.findConversationIdByUserIds(user1.getId(), user3.getId());

        // Then
        assertNotNull(conversationIds);
        assertEquals(1, conversationIds.size());
        assertEquals(conversation2.getId().intValue(), conversationIds.get(0).intValue());
    }

    @Test
    public void testFindByConversationId() {
        // When
        List<ConversationMembers> members = conversationMembersRepository.findByConversationId(conversation1.getId());

        // Then
        assertNotNull(members);
        assertEquals(2, members.size());
    }

    @Test
    public void testFindAllFriendsNotInConversation() {
        // When
        List<User> friendsNotInConversation = conversationMembersRepository.findAllFriendsNotInConversation(user1.getId(), conversation1.getId());

        // Then
        assertNotNull(friendsNotInConversation);
        assertEquals(1, friendsNotInConversation.size());
        assertEquals(user3.getId(), friendsNotInConversation.get(0).getId());
    }
}