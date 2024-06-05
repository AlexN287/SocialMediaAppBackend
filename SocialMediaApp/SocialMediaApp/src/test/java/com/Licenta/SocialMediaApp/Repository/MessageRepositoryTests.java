package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Content;
import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Model.Message;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class MessageRepositoryTests {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ContentRepository contentRepository;

    private Conversation conversation;

    @BeforeEach
    public void setUp() {
        // Initialize and save Conversation
        conversation = new Conversation();
        conversation = conversationRepository.save(conversation);

        // Initialize and save Messages
        Message message1 = new Message();
        Content content1 = new Content();

        content1.setTextContent("Message 1");
        message1.setContent(content1);
        contentRepository.save(content1);
        message1.setConversation(conversation);
        message1.setTimestamp(LocalDateTime.now().minusMinutes(10));
        messageRepository.save(message1);

        Message message2 = new Message();
        Content content2 = new Content();

        content2.setTextContent("Message 2");
        message2.setContent(content2);
        contentRepository.save(content2);
        message2.setConversation(conversation);
        message2.setTimestamp(LocalDateTime.now().minusMinutes(5));
        messageRepository.save(message2);

        Message message3 = new Message();
        Content content3 = new Content();

        content3.setTextContent("Message 3");
        message3.setContent(content3);
        contentRepository.save(content3);
        message3.setConversation(conversation);
        message3.setTimestamp(LocalDateTime.now());
        messageRepository.save(message3);
    }

    @Test
    public void testFindByConversationId() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Message> page = messageRepository.findByConversationId(conversation.getId(), pageable);

        // Then
        assertNotNull(page);
        assertEquals(2, page.getSize());
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getContent().size());
    }

    @Test
    public void testFindLatestMessageByConversationId() {
        // When
        Optional<Message> latestMessage = messageRepository.findLatestMessageByConversationId(conversation.getId());

        // Then
        assertTrue(latestMessage.isPresent());
        assertEquals("Message 3", latestMessage.get().getContent().getTextContent());
    }
}
