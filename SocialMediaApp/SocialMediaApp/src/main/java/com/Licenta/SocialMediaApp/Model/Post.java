package com.Licenta.SocialMediaApp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "content_id")
    private Content content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

/*    @Column(name = "like_nr")
    private int likeNr;

    @Column(name = "comments_nr")
    private int commentsNr;*/

    public Post(User user, Content content) {
        this.user = user;
        this.content = content;
    }
}
