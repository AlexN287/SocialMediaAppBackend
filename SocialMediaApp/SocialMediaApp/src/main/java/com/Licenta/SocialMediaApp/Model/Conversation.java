package com.Licenta.SocialMediaApp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation")
@Getter
@Setter
@NoArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long id;

    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "conversation_image_path")
    private String conversationImagePath;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "is_group")
    private boolean isGroup;
}
