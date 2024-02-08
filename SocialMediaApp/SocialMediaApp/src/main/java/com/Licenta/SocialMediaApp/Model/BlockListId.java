package com.Licenta.SocialMediaApp.Model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
@Embeddable
public class BlockListId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "blocked_user_id", referencedColumnName = "user_id")
    private User blockedUser;
}
