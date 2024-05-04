package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Bucket;
import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Exceptions.ConversationAlreadyExistsException;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Model.ConversationMembers;
import com.Licenta.SocialMediaApp.Model.ConversationMembersId;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.ConversationMembersRepository;
import com.Licenta.SocialMediaApp.Repository.ConversationRepository;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.ConversationService;
import com.Licenta.SocialMediaApp.Service.FriendsListService;
import com.Licenta.SocialMediaApp.Service.UserService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;
    private final ConversationMembersRepository conversationMembersRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final FriendsListService friendsListService;
    public ConversationServiceImpl(ConversationRepository conversationRepository, UserService userService,
                                   ConversationMembersRepository conversationMembersRepository, S3Service s3Service,
                                   UserRepository userRepository, S3Bucket s3Bucket, FriendsListService friendsListService) {
        this.conversationRepository = conversationRepository;
        this.userService = userService;
        this.conversationMembersRepository = conversationMembersRepository;
        this.s3Service = s3Service;
        this.userRepository = userRepository;
        this.friendsListService = friendsListService;
    }

    @Override
    public List<Conversation> getUsersConversation(String jwt) {
        User loggedUser = userService.findUserByJwt(jwt);
        List<Conversation> conversations =  conversationRepository.findConversationsByUserIdOrderedByMostRecent(loggedUser.getId());

        conversations.forEach(conversation -> {
            if (conversation.getName() == null || conversation.getConversationImagePath() == null) {
                // Identify as a private conversation
                // Fetch the other user's username in this conversation and update
                User user = conversationMembersRepository.findOtherUserInPrivateConversation(conversation.getId(), loggedUser.getId());
                conversation.setName(user.getUsername());
                // Set a default image or fetch from the other user's profile
                conversation.setConversationImagePath(user.getProfileImagePath());
            }
        });

        return conversations;
    }

    @Override
    @Transactional
    public void createPrivateConversation(int userId, String jwt) {
        User loggedUser = userService.findUserByJwt(jwt);
        List<Integer> existingConversations = conversationMembersRepository.findConversationIdByUserIds(loggedUser.getId(), userId);

        if (!existingConversations.isEmpty()) {
            // Throw the custom exception if a conversation already exists
            throw new ConversationAlreadyExistsException("A private conversation between the specified users already exists.");
        }

        // Proceed to create a new conversation if none exists
        Conversation newConversation = new Conversation();
        newConversation.setCreatedAt(LocalDateTime.now());
        newConversation.setGroup(false);
        conversationRepository.save(newConversation);

        // Add the logged user to the conversation
        addMemberToConversation(newConversation, loggedUser);
        // Add the other user to the conversation
        User otherUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        addMemberToConversation(newConversation, otherUser);
    }

    private void addMemberToConversation(Conversation conversation, User user) {
        ConversationMembers conversationMembers = new ConversationMembers();
        ConversationMembersId conversationMembersId = new ConversationMembersId();
        conversationMembersId.setConversation(conversation);
        conversationMembersId.setUser(user);
        conversationMembers.setId(conversationMembersId);
        conversationMembersRepository.save(conversationMembers);
    }
    @Override
    @Transactional
    public void createGroupConversation(String name,
                                        MultipartFile groupImage,
                                        List<Integer> members,
                                        String jwt) throws IOException {
        Conversation newConversation = new Conversation();
        newConversation.setName(name);
        newConversation.setCreatedAt(LocalDateTime.now());
        newConversation.setGroup(false);
        // Save the conversation to generate its ID
        newConversation = conversationRepository.save(newConversation);

        // Assume S3Service is injected and handles image storage
        if (groupImage != null && !groupImage.isEmpty()) {
            String groupImagePath = s3Service.generateGroupImageKey(newConversation.getId(), groupImage);
            newConversation.setConversationImagePath(groupImagePath);
            s3Service.putObject(groupImagePath, groupImage.getBytes());
        }

        User loggedUser = userService.findUserByJwt(jwt);
        addMemberToConversation(newConversation, loggedUser);
        // Add each member to the conversation
        for (Integer userId : members) {
            User user = new User();
            user.setId(userId);
            addMemberToConversation(newConversation, user);
        }

        // Update conversation after adding members and potentially setting the image path
        conversationRepository.save(newConversation);
    }

    @Override
    public void addGroupMember(int conversationId, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with ID: " + conversationId));

        // Check if the user is already a member of the conversation
        boolean isMember = conversationMembersRepository.existsById(new ConversationMembersId(conversation, user));
        if (!isMember) {
            ConversationMembers conversationMembers = new ConversationMembers();
            ConversationMembersId conversationMembersId = new ConversationMembersId();
            conversationMembersId.setConversation(conversation);
            conversationMembersId.setUser(user);
            conversationMembers.setId(conversationMembersId);
            conversationMembersRepository.save(conversationMembers);
        } else {
            throw new RuntimeException("User already a member of the conversation.");
        }
    }

    @Override
    public void removeGroupMember(int conversationId, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with ID: " + conversationId));

        // Check if the user is actually a member of the conversation to be removed
        boolean isMember = conversationMembersRepository.existsById(new ConversationMembersId(conversation, user));
        if (isMember) {
            ConversationMembersId conversationMembersId = new ConversationMembersId(conversation, user);
            conversationMembersRepository.deleteById(conversationMembersId);
        } else {
            throw new RuntimeException("User is not a member of the conversation.");
        }
    }

    @Override
    public void leaveGroup(int conversationId, String jwt) {
        User loggedUser = userService.findUserByJwt(jwt);

        removeGroupMember(conversationId, loggedUser.getId());
    }

    @Override
    public byte[] loadConversationImage(int conversationId, String jwt) throws IOException {
        System.out.println("Conversation Image");

        User loggedUser = userService.findUserByJwt(jwt);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with ID: " + conversationId));

        String key;

        if(conversation.getConversationImagePath() == null)
        {
            User user = conversationMembersRepository.findOtherUserInPrivateConversation(conversationId, loggedUser.getId());
            key = user.getProfileImagePath();
        }
        else {
            key = conversation.getConversationImagePath();
        }
        return s3Service.getObject(key);
    }

    @Override
    public List<User> getMembersByConversationId(int conversationId) {
        List<ConversationMembers> members = conversationMembersRepository.findByConversationId(conversationId);

        return members.stream()
                .map(member -> member.getId().getUser())
                // Access User through the embedded ID
                .collect(Collectors.toList());
    }
    public List<UserResponse> findFriendsNotInConversation(String jwt, int conversationId) {
        User loggedUser = userService.findUserByJwt(jwt);

        List<User> friendsNotInConversation = conversationMembersRepository.findAllFriendsNotInConversation(loggedUser.getId(), conversationId);
        // Using Utils.convertToUserResponse directly
        return friendsNotInConversation.stream()
                .map(Utils::convertToUserResponse)
                .collect(Collectors.toList());
    }
}
