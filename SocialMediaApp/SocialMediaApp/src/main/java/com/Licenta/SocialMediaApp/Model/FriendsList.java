package com.Licenta.SocialMediaApp.Model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "friends_list")
@Getter
@Setter
@NoArgsConstructor
public class FriendsList {
    @EmbeddedId
    private FriendsListId id;
}
