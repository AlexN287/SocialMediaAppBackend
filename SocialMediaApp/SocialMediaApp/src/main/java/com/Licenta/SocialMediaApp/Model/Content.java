package com.Licenta.SocialMediaApp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "content")
@Getter
@Setter
@NoArgsConstructor
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @Column(name = "text_content")
    private String textContent;

    @Column(name = "file_path")
    private String filePath;

    public Content(String textContent, String filePath) {
        this.textContent = textContent;
        this.filePath = filePath;
    }
}
