package com.Licenta.SocialMediaApp.Model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Embeddable
@Setter
@Getter
public class FriendsListId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "user_id1", referencedColumnName = "user_id")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user_id2", referencedColumnName = "user_id")
    private User user2;
}
