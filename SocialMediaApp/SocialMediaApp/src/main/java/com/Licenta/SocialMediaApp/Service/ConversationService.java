package com.Licenta.SocialMediaApp.Service;


import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.Conversation;
import com.Licenta.SocialMediaApp.Model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ConversationService {
    List<Conversation> getUsersConversation(String jwt);
    void createPrivateConversation(Long userId, String jwt);
    void createGroupConversation(String name, MultipartFile groupImage, List<Long> members, String jwt) throws IOException;
    void addGroupMember(Long conversationId, Long userId);
    void removeGroupMember(Long conversationId, Long userId);
    void leaveGroup(Long conversationId, String jwt);
    byte[] loadConversationImage(Long conversationId, String jwt) throws IOException;
    List<User> getMembersByConversationId(Long conversationId);
    List<UserResponse> findFriendsNotInConversation(String jwt, Long conversationId);
    //public List<Object> getConversationContent(Long conversationId);
    List<Conversation> searchUsersConversation(String jwt, String term);
}
