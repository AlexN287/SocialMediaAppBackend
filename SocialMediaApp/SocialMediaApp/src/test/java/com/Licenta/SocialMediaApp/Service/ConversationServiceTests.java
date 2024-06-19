package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Model.*;
import com.Licenta.SocialMediaApp.Model.BodyResponse.ConversationResponse;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Repository.*;
import com.Licenta.SocialMediaApp.Service.ServiceImpl.ConversationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConversationServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ConversationMembersRepository conversationMembersRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private FriendsListRepository friendsListRepository;

    @Mock
    private FriendsListService friendsListService;

    @Mock
    private UserService userService;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    private User user;
    private User otherUser;
    private Conversation conversation;
    private Message message;
    private FriendsList friendsList;

    @BeforeEach
    public void setUp() {
        // Initialize User
        user = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user.setId(1L);
        otherUser = new User("jane_doe", "password123", "jane_doe@example.com", "/profile/path2");
        otherUser.setId(2L);

        // Initialize Conversation
        conversation = new Conversation();
        conversation.setId(1L);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setGroup(false);

        // Initialize Message
        message = new Message();
        message.setId(1L);
        Content content = new Content();
        content.setTextContent("Hello");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        lenient().when(userService.findUserByJwt(anyString())).thenReturn(user);
        lenient().when(conversationRepository.findById(anyLong())).thenReturn(Optional.of(conversation));
        lenient().when(messageRepository.findLatestMessageByConversationId(anyLong())).thenReturn(Optional.of(message));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user), Optional.of(otherUser));
    }

    @Test
    public void testGetUsersConversation() {
        // Given
        when(conversationRepository.findConversationsByUserIdOrderedByMostRecent(anyLong())).thenReturn(List.of(conversation));
        when(conversationMembersRepository.findOtherUserInPrivateConversation(anyLong(), anyLong())).thenReturn(otherUser);
        when(friendsListService.isFriendshipExists(anyLong(), anyLong())).thenReturn(true);

        // When
        List<ConversationResponse> result = conversationService.getUsersConversation("valid.jwt.token");

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(conversation.getId(), result.get(0).getId());
        verify(conversationRepository, times(1)).findConversationsByUserIdOrderedByMostRecent(user.getId());
    }

    @Test
    public void testCreatePrivateConversation() {
        // Given
        when(conversationMembersRepository.findConversationIdByUserIds(anyLong(), anyLong())).thenReturn(Collections.emptyList());
        when(userService.findUserByJwt(anyString())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(otherUser));
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);

        // When
        conversationService.createPrivateConversation(otherUser.getId(), "valid.jwt.token");

        // Then
        verify(conversationRepository, times(1)).save(any(Conversation.class));
        verify(conversationMembersRepository, times(2)).save(any(ConversationMembers.class));
    }

    @Test
    public void testCreateGroupConversation() throws IOException {
        // Given
        MockMultipartFile groupImage = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());
        when(s3Service.generateGroupImageKey(anyLong(), any())).thenReturn("/image/path");
        doNothing().when(s3Service).putObject(anyString(), any(byte[].class));
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);

        // When
        conversationService.createGroupConversation("Group Name", groupImage, List.of(otherUser.getId()), "valid.jwt.token");

        // Then
        verify(conversationRepository, times(2)).save(any(Conversation.class));
        verify(conversationMembersRepository, times(2)).save(any(ConversationMembers.class));
        verify(s3Service, times(1)).putObject(anyString(), any(byte[].class));
    }

    @Test
    public void testAddGroupMember() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(otherUser));
        when(conversationRepository.findById(anyLong())).thenReturn(Optional.of(conversation));
        when(conversationMembersRepository.existsById(any(ConversationMembersId.class))).thenReturn(false);

        // When
        conversationService.addGroupMember(conversation.getId(), otherUser.getId());

        // Then
        verify(conversationMembersRepository, times(1)).save(any(ConversationMembers.class));
    }

    @Test
    public void testRemoveGroupMember() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(otherUser));
        when(conversationRepository.findById(anyLong())).thenReturn(Optional.of(conversation));
        when(conversationMembersRepository.existsById(any(ConversationMembersId.class))).thenReturn(true);

        // When
        conversationService.removeGroupMember(conversation.getId(), otherUser.getId());

        // Then
        verify(conversationMembersRepository, times(1)).deleteById(any(ConversationMembersId.class));
    }

    @Test
    public void testLeaveGroup() {
        // Given
        when(userService.findUserByJwt(anyString())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(conversationMembersRepository.existsById(any(ConversationMembersId.class))).thenReturn(true);

        // When
        conversationService.leaveGroup(conversation.getId(), "valid.jwt.token");

        // Then
        verify(conversationMembersRepository, times(1)).deleteById(any(ConversationMembersId.class));
    }

    @Test
    public void testLoadConversationImage() throws IOException {
        // Given
        when(s3Service.getObject(anyString())).thenReturn("image content".getBytes());
        when(conversationMembersRepository.findOtherUserInPrivateConversation(anyLong(), anyLong())).thenReturn(otherUser);

        // When
        byte[] image = conversationService.loadConversationImage(conversation.getId(), "valid.jwt.token");

        // Then
        assertNotNull(image);
        assertEquals("image content", new String(image));
        verify(s3Service, times(1)).getObject(anyString());
    }
    @Test
    public void testGetMembersByConversationId() {
        // Given
        ConversationMembers conversationMember = new ConversationMembers();
        conversationMember.setId(new ConversationMembersId(conversation, user));
        when(conversationMembersRepository.findByConversationId(anyLong())).thenReturn(List.of(conversationMember));

        // When
        List<User> members = conversationService.getMembersByConversationId(conversation.getId());

        // Then
        assertNotNull(members);
        assertFalse(members.isEmpty());
        assertEquals(1, members.size());
        assertEquals(user.getId(), members.get(0).getId());
        verify(conversationMembersRepository, times(1)).findByConversationId(conversation.getId());
    }

    @Test
    public void testFindFriendsNotInConversation() {
        // Given
        when(conversationMembersRepository.findAllFriendsNotInConversation(anyLong(), anyLong())).thenReturn(List.of(otherUser));

        // When
        List<UserResponse> result = conversationService.findFriendsNotInConversation("valid.jwt.token", conversation.getId());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(otherUser.getUsername(), result.get(0).getUsername());
        verify(conversationMembersRepository, times(1)).findAllFriendsNotInConversation(anyLong(), anyLong());
    }

    @Test
    public void testSearchUsersConversation() {
        // Given
        when(conversationRepository.searchConversationsByUserId(anyLong(), anyString())).thenReturn(List.of(conversation));
        when(conversationMembersRepository.findOtherUserInPrivateConversation(anyLong(), anyLong())).thenReturn(otherUser);
        when(friendsListService.isFriendshipExists(anyLong(), anyLong())).thenReturn(true);

        // When
        List<ConversationResponse> result = conversationService.searchUsersConversation("valid.jwt.token", "search term");

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(conversation.getId(), result.get(0).getId());
        verify(conversationRepository, times(1)).searchConversationsByUserId(anyLong(), anyString());
    }
}
