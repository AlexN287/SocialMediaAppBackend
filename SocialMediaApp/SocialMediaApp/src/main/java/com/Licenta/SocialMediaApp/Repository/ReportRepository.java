package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    void deleteById(int reportId);
    List<Report> findByPostId(int postId);
}
