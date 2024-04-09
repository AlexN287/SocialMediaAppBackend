package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByConversationId(int conversationId);
}
