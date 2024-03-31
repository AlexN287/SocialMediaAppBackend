package com.Licenta.SocialMediaApp.Model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "conversation_members")
@Getter
@Setter
@NoArgsConstructor
public class ConversationMembers {
    @EmbeddedId
    private ConversationMembersId id;
}
