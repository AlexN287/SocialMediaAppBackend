package com.Licenta.SocialMediaApp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/*@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private int id;

    private String name;

    private Date timestamp;

    @OneToOne
    @JoinColumn(name = "conversation_id", referencedColumnName = "conversation_id")
    private Conversation conversation;

    public Group(String name, Date timestamp, Conversation conversation) {
        this.name = name;
        this.timestamp = timestamp;
        this.conversation = conversation;
    }
}*/
