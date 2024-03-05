package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Model.BodyRequests.GroupRequest;
import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Model.ConversationMembers;
import com.Licenta.SocialMediaApp.Model.ConversationMembersId;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.ConversationMembersRepository;
import com.Licenta.SocialMediaApp.Repository.ConversationRepository;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.ConversationService;
import com.Licenta.SocialMediaApp.Service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;
    private final ConversationMembersRepository conversationMembersRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    private final S3Service s3Service;
    public ConversationServiceImpl(ConversationRepository conversationRepository, UserService userService,
                                   ConversationMembersRepository conversationMembersRepository, S3Service s3Service,
                                   UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.userService = userService;
        this.conversationMembersRepository = conversationMembersRepository;
        this.s3Service = s3Service;
        this.userRepository = userRepository;
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
        Conversation newConversation = new Conversation();
        newConversation.setCreatedAt(LocalDateTime.now());
        newConversation = conversationRepository.save(newConversation);

        User loggedUser = userService.findUserByJwt(jwt);

        ConversationMembers conversationMembers = new ConversationMembers();
        ConversationMembersId conversationMembersId = new ConversationMembersId();
        conversationMembersId.setConversation(newConversation);
        conversationMembersId.setUser(loggedUser);
        conversationMembers.setId(conversationMembersId);
        conversationMembersRepository.save(conversationMembers);

        ConversationMembersId conversationMembersId1 = new ConversationMembersId();
        conversationMembersId1.setConversation(newConversation);
        User user = new User();
        user.setId(userId);
        conversationMembersId1.setUser(user);
        ConversationMembers conversationMembers1 = new ConversationMembers();
        conversationMembers1.setId(conversationMembersId1);
        conversationMembersRepository.save(conversationMembers1);
    }

    @Override
    @Transactional
    public void createGroupConversation(GroupRequest groupRequest) throws IOException {
        Conversation newConversation = new Conversation();
        newConversation.setName(groupRequest.getName());
        newConversation.setCreatedAt(groupRequest.getCreatedAt());

        newConversation = conversationRepository.save(newConversation);

        String groupImagePath = s3Service.generateGroupImageKey(newConversation.getId(), groupRequest.getGroupImage());
        newConversation.setConversationImagePath(groupImagePath);

        s3Service.putObject(groupImagePath, groupRequest.getGroupImage().getBytes());

        for(User user : groupRequest.getMembers())
        {
            ConversationMembers conversationMembers = new ConversationMembers();
            ConversationMembersId conversationMembersId = new ConversationMembersId();
            conversationMembersId.setConversation(newConversation);
            conversationMembersId.setUser(user);
            conversationMembers.setId(conversationMembersId);

            conversationMembersRepository.save(conversationMembers);
        }
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
            conversationMembersRepository.deleteById(new ConversationMembersId(conversation, user));
        } else {
            throw new RuntimeException("User is not a member of the conversation.");
        }
    }

    @Override
    public void leaveGroup(int conversationId, String jwt) {
        User loggedUser = userService.findUserByJwt(jwt);

        removeGroupMember(conversationId, loggedUser.getId());
    }
}
