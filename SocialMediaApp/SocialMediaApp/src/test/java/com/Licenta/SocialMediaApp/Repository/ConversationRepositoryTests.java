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
public class ConversationRepositoryTests {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationMembersRepository conversationMembersRepository;

    @Autowired
    private ContentRepository contentRepository;

    private User user1;
    private User user2;
    private Conversation conversation1;
    private Conversation conversation2;

    @BeforeEach
    public void setUp() {
        // Initialize and save Users
        user1 = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user1 = userRepository.save(user1);

        user2 = new User("jane_doe", "password456", "jane_doe@example.com", "/profile/path2");
        user2 = userRepository.save(user2);

        // Initialize and save Conversations
        conversation1 = new Conversation();
        conversation1.setName("Conversation 1");
        conversation1 = conversationRepository.save(conversation1);

        conversation2 = new Conversation();
        conversation2.setName("Conversation 2");
        conversation2 = conversationRepository.save(conversation2);

        // Initialize and save ConversationMembers
        ConversationMembers cm1 = new ConversationMembers();
        cm1.setId(new ConversationMembersId(conversation1, user1));
        conversationMembersRepository.save(cm1);

        ConversationMembers cm2 = new ConversationMembers();
        cm2.setId(new ConversationMembersId(conversation2, user1));
        conversationMembersRepository.save(cm2);

        ConversationMembers cm3 = new ConversationMembers();
        cm3.setId(new ConversationMembersId(conversation2, user2));
        conversationMembersRepository.save(cm3);

        // Initialize and save Messages
        Message message1 = new Message();
        Content content1 = new Content();
        content1.setTextContent("Hello");
        contentRepository.save(content1);
        message1.setContent(content1);
        message1.setConversation(conversation1);
        message1.setTimestamp(LocalDateTime.now().minusDays(1));
        messageRepository.save(message1);

        Message message2 = new Message();
        Content content2 = new Content();
        content2.setTextContent("Hi");
        contentRepository.save(content2);
        message2.setContent(content2);
        message2.setConversation(conversation2);
        message2.setTimestamp(LocalDateTime.now());
        messageRepository.save(message2);
    }

    @Test
    public void testFindConversationsByUserIdOrderedByMostRecent() {
        // When
        List<Conversation> conversations = conversationRepository.findConversationsByUserIdOrderedByMostRecent(user1.getId());

        // Then
        assertNotNull(conversations);
        assertEquals(2, conversations.size());
        assertEquals(conversation2.getId(), conversations.get(0).getId()); // Most recent conversation first
        assertEquals(conversation1.getId(), conversations.get(1).getId());
    }

    @Test
    public void testSearchConversationsByUserId() {
        // When
        List<Conversation> conversations = conversationRepository.searchConversationsByUserId(user1.getId(), "Conversation 1");

        // Then
        assertNotNull(conversations);
        assertEquals(1, conversations.size());
        assertEquals(conversation1.getId(), conversations.get(0).getId());

        List<Conversation> conversations2 = conversationRepository.searchConversationsByUserId(user1.getId(), "Conversation");
        assertNotNull(conversations);
        assertEquals(2, conversations2.size());
    }
}
