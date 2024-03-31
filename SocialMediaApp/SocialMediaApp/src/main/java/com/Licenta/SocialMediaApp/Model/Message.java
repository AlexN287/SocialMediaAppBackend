package com.Licenta.SocialMediaApp.Model;

import com.Licenta.SocialMediaApp.Model.Enums.MessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "message")
@Getter
@Setter
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private int id;

    @OneToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @OneToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @OneToOne
    @JoinColumn(name = "content_id")
    private Content content;

    @Column(name = "message_timestamp")
    private LocalDateTime timestamp;

    @Transient
    private MessageType messageType;
}
