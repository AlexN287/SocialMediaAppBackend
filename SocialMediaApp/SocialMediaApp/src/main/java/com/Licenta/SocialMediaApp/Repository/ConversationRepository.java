package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    @Query("SELECT cm.id.conversation FROM ConversationMembers cm WHERE cm.id.user.id = :userId ORDER BY cm.id.conversation.lastUpdated DESC")
    List<Conversation> findConversationsByUserIdOrderedByMostRecent(int userId);
}
