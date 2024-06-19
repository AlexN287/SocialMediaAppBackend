package com.Licenta.SocialMediaApp.Model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendsListId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "user_id1", referencedColumnName = "user_id")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user_id2", referencedColumnName = "user_id")
    private User user2;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        FriendsListId that = (FriendsListId) o;
//        return Objects.equals(user1, that.user1) &&
//                Objects.equals(user2, that.user2);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(user1, user2);
//    }
}
