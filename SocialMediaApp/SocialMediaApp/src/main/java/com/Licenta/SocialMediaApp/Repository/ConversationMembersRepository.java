package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.ConversationMembers;
import com.Licenta.SocialMediaApp.Model.ConversationMembersId;
import com.Licenta.SocialMediaApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationMembersRepository extends JpaRepository<ConversationMembers, ConversationMembersId> {
    @Query("SELECT u FROM ConversationMembers cm JOIN cm.id.conversation c JOIN cm.id.user u WHERE c.id = :conversationId AND u.id <> :userId")
    User findOtherUserInPrivateConversation(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
    boolean existsById(ConversationMembersId conversationMembersId);
    void deleteById(ConversationMembersId conversationMembersId);
    @Query("SELECT cm.id.conversation.id FROM ConversationMembers cm WHERE cm.id.user.id IN (:userId1, :userId2) " +
            "GROUP BY cm.id.conversation.id HAVING COUNT(cm.id.conversation.id) = 2")
    List<Integer> findConversationIdByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    @Query("SELECT cm FROM ConversationMembers cm WHERE cm.id.conversation.id = :conversationId")
    List<ConversationMembers> findByConversationId(@Param("conversationId") Long conversationId);
    @Query("SELECT u FROM User u WHERE u.id IN (SELECT CASE WHEN fl.id.user1.id = :userId THEN fl.id.user2.id ELSE fl.id.user1.id END FROM FriendsList fl WHERE fl.id.user1.id = :userId OR fl.id.user2.id = :userId) AND u.id NOT IN (SELECT cm.id.user.id FROM ConversationMembers cm WHERE cm.id.conversation.id = :conversationId)")
    List<User> findAllFriendsNotInConversation(@Param("userId") Long userId, @Param("conversationId") Long conversationId);


}
