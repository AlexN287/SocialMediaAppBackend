package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
