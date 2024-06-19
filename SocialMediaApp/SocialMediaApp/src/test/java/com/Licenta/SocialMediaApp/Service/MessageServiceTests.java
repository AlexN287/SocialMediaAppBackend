package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Model.Content;
import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Model.Message;
import com.Licenta.SocialMediaApp.Repository.ContentRepository;
import com.Licenta.SocialMediaApp.Repository.ConversationRepository;
import com.Licenta.SocialMediaApp.Repository.MessageRepository;
import com.Licenta.SocialMediaApp.Service.ServiceImpl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTests {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private MessageServiceImpl messageService;

    private Message message;
    private Conversation conversation;
    private Content content;

    @BeforeEach
    public void setUp() {
        // Initialize Content
        content = new Content();
        content.setId(1L);
        content.setTextContent("Sample text");
        content.setFilePath("/file/path");

        // Initialize Conversation
        conversation = new Conversation();
        conversation.setId(1L);

        // Initialize Message
        message = new Message();
        message.setId(1L);
        message.setConversation(conversation);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        lenient().when(messageRepository.save(any(Message.class))).thenReturn(message);
        lenient().when(conversationRepository.findById(anyLong())).thenReturn(Optional.of(conversation));
        lenient().when(contentRepository.save(any(Content.class))).thenReturn(content);
    }

    @Test
    public void testSendMessage() {
        // Given
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(conversationRepository.findById(anyLong())).thenReturn(Optional.of(conversation));
        when(contentRepository.save(any(Content.class))).thenReturn(content);

        // When
        Message newMessage = messageService.sendMessage(message);

        // Then
        assertNotNull(newMessage);
        assertEquals(message.getId(), newMessage.getId());
        verify(messageRepository, times(2)).save(any(Message.class)); // Initial save and after setting content
        verify(contentRepository, times(1)).save(any(Content.class));
    }

    @Test
    public void testGetMessagesByConversationId() {
        // Given
        Page<Message> page = new PageImpl<>(Collections.singletonList(message));
        when(messageRepository.findByConversationId(anyLong(), any(Pageable.class))).thenReturn(page);

        // When
        Page<Message> result = messageService.getMessagesByConversationId(1L, Pageable.unpaged());

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(messageRepository, times(1)).findByConversationId(anyLong(), any(Pageable.class));
    }

    @Test
    public void testGetLastMessageByConversationId() {
        // Given
        when(messageRepository.findLatestMessageByConversationId(anyLong())).thenReturn(Optional.of(message));

        // When
        Optional<Message> result = messageService.getLastMessageByConversationId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(message.getId(), result.get().getId());
        verify(messageRepository, times(1)).findLatestMessageByConversationId(anyLong());
    }

    @Test
    public void testSendFile() throws IOException {
        // Given
        String textContent = "Sample text";
        Long conversationId = 1L;
        Long senderId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());

        // Mock S3Service
        when(s3Service.generateConversationFileKey(anyLong(), anyLong(), any())).thenReturn("/file/path");
        doNothing().when(s3Service).putObject(anyString(), any(byte[].class));

        // When
        String key = messageService.sendFile(file, textContent, conversationId, senderId);

        // Then
        assertNotNull(key);
        assertEquals("/file/path", key);
        verify(contentRepository, times(2)).save(any(Content.class));
        verify(messageRepository, times(1)).save(any(Message.class));
        verify(s3Service, times(1)).putObject(anyString(), any(byte[].class));
    }

    @Test
    public void testGetMessageMedia() throws Exception {
        // Given
        String filePath = "/file/path";
        when(s3Service.getObject(anyString())).thenReturn("some media content".getBytes());

        // When
        byte[] media = messageService.getMessageMedia(filePath);

        // Then
        assertNotNull(media);
        assertEquals("some media content", new String(media));
        verify(s3Service, times(1)).getObject(filePath);
    }

    @Test
    public void testGetMediaKey() throws Exception {
        // Given
        when(messageRepository.findById(anyLong())).thenReturn(Optional.of(message));

        // When
        String mediaKey = messageService.getMediaKey(1L);

        // Then
        assertNotNull(mediaKey);
        assertEquals("/file/path", mediaKey);
        verify(messageRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetLastMessageByConversationIdAndSenderId() {
        // Given
        when(messageRepository.findLastMessageByConversationIdAndSenderId(anyLong(), anyLong())).thenReturn(Optional.of(message));

        // When
        Optional<Message> result = messageService.getLastMessageByConversationIdAndSenderId(1L, 1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(message.getId(), result.get().getId());
        verify(messageRepository, times(1)).findLastMessageByConversationIdAndSenderId(anyLong(), anyLong());
    }
}
