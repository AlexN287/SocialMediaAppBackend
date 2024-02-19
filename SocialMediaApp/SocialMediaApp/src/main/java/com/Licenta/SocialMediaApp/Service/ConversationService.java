package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.Conversation;

import java.util.List;

public interface ConversationService {
    List<Conversation> getUsersConversation(String jwt);
}
