package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Integer> {
}
