package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.ConversationRepository;
import com.Licenta.SocialMediaApp.Service.ConversationService;
import com.Licenta.SocialMediaApp.Service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserService userService;

    public ConversationServiceImpl(ConversationRepository conversationRepository, UserService userService) {
        this.conversationRepository = conversationRepository;
        this.userService = userService;
    }

    @Override
    public List<Conversation> getUsersConversation(String jwt) {
        User loggedUser = userService.findUserByJwt(jwt);
        return conversationRepository.findConversationsByUserIdOrderedByMostRecent(loggedUser.getId());
    }
}
