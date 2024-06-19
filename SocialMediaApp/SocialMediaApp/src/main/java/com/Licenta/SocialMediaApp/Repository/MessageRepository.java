package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByConversationId(Long conversationId, Pageable pageable);
    @Query(value = "SELECT * FROM Message m WHERE m.conversation_id = :conversationId ORDER BY m.message_timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<Message> findLatestMessageByConversationId(@Param("conversationId") Long conversationId);
    @Query(value = "SELECT * FROM Message m WHERE m.conversation_id = :conversationId AND m.sender_id = :senderId ORDER BY m.message_timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<Message> findLastMessageByConversationIdAndSenderId(@Param("conversationId") Long conversationId, @Param("senderId") Long senderId);
}
