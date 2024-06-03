package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT cm.id.conversation FROM ConversationMembers cm WHERE cm.id.user.id = :userId ORDER BY cm.id.conversation.lastUpdated DESC")
    List<Conversation> findConversationsByUserIdOrderedByMostRecent(Long userId);

    @Query("SELECT c FROM Conversation c " +
            "JOIN ConversationMembers cm ON cm.id.conversation.id = c.id " +
            "WHERE cm.id.user.id = :userId " +
            "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(c.lastMessage) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR (c.isGroup = false AND EXISTS (" +
            "  SELECT 1 FROM ConversationMembers cm2 " +
            "  WHERE cm2.id.conversation.id = c.id " +
            "  AND cm2.id.user.id != :userId " +
            "  AND LOWER(cm2.id.user.username) LIKE LOWER(CONCAT('%', :term, '%'))" +
            "))) " +
            "ORDER BY c.lastUpdated DESC")
    List<Conversation> searchConversationsByUserId(@Param("userId") Long userId, @Param("term") String term);

}
