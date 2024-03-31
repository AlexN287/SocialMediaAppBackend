package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.ConversationMembers;
import com.Licenta.SocialMediaApp.Model.ConversationMembersId;
import com.Licenta.SocialMediaApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationMembersRepository extends JpaRepository<ConversationMembers, Integer> {
    @Query("SELECT u FROM ConversationMembers cm JOIN cm.id.conversation c JOIN cm.id.user u WHERE c.id = :conversationId AND u.id <> :userId")
    User findOtherUserInPrivateConversation(@Param("conversationId") int conversationId, @Param("userId") int userId);
    boolean existsById(ConversationMembersId conversationMembersId);
    void deleteById(ConversationMembersId conversationMembersId);
    @Query("SELECT cm.id.conversation.id FROM ConversationMembers cm WHERE cm.id.user.id IN (:userId1, :userId2) " +
            "GROUP BY cm.id.conversation.id HAVING COUNT(cm.id.conversation.id) = 2")
    List<Integer> findConversationIdByUserIds(@Param("userId1") int userId1, @Param("userId2") int userId2);

}
