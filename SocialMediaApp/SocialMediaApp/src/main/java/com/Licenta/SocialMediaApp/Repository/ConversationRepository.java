package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId ORDER BY c.lastUpdated DESC")
    List<Conversation> findConversationsByUserIdOrderedByMostRecent(@Param("userId") int userId);
}
