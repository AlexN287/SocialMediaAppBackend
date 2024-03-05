package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.BodyRequests.GroupRequest;
import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Model.ConversationMembers;
import com.Licenta.SocialMediaApp.Model.ConversationMembersId;

import java.io.IOException;
import java.util.List;

public interface ConversationService {
    List<Conversation> getUsersConversation(String jwt);
    void createPrivateConversation(int userId, String jwt);
    void createGroupConversation(GroupRequest groupRequest) throws IOException;
    void addGroupMember(int conversationId, int userId);
    void removeGroupMember(int conversationId, int userId);
    void leaveGroup(int conversationId, String jwt);
}
