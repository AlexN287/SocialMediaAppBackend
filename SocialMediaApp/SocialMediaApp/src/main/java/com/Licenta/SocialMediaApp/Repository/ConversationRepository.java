package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c " +
            "JOIN ConversationMembers cm ON cm.id.conversation.id = c.id " +
            "LEFT JOIN Message m ON m.conversation.id = c.id " +
            "WHERE cm.id.user.id = :userId " +
            "AND m.timestamp = (SELECT MAX(m2.timestamp) FROM Message m2 WHERE m2.conversation.id = c.id) " +
            "ORDER BY m.timestamp DESC")
    List<Conversation> findConversationsByUserIdOrderedByMostRecent(@Param("userId") Long userId);
    @Query("SELECT c FROM Conversation c " +
            "JOIN ConversationMembers cm ON cm.id.conversation.id = c.id " +
            "WHERE cm.id.user.id = :userId " +
            "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR (c.isGroup = false AND EXISTS (" +
            "  SELECT 1 FROM ConversationMembers cm2 " +
            "  WHERE cm2.id.conversation.id = c.id " +
            "  AND cm2.id.user.id != :userId " +
            "  AND LOWER(cm2.id.user.username) LIKE LOWER(CONCAT('%', :term, '%'))" +
            "))) ")
    List<Conversation> searchConversationsByUserId(@Param("userId") Long userId, @Param("term") String term);

}
