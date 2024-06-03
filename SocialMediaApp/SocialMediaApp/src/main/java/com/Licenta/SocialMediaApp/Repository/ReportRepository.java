package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    void deleteById(Long reportId);
    List<Report> findByPostId(Long postId);
}
