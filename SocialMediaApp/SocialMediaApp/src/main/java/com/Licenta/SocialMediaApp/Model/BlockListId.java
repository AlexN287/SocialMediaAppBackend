package com.Licenta.SocialMediaApp.Model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockListId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "blocked_user_id", referencedColumnName = "user_id")
    private User blockedUser;
}
