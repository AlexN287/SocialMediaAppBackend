package com.Licenta.SocialMediaApp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "friendship_request")
@Getter
@Setter
@NoArgsConstructor
public class FriendshipRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    private int id;
    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;

    private String Status;

    public FriendshipRequest(User sender, User receiver, String status) {
        this.sender = sender;
        this.receiver = receiver;
        Status = status;
    }
}
