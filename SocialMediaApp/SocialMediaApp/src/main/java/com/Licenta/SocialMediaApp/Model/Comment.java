package com.Licenta.SocialMediaApp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "content_id")
    private Content content;

    @Column(name = "comment_timestamp")
    private LocalDateTime timestamp;

    public Comment(User user, Post post, Content content, LocalDateTime commentTimestamp) {
        this.user = user;
        this.post = post;
        this.content = content;
        this.timestamp = commentTimestamp;
    }
}
