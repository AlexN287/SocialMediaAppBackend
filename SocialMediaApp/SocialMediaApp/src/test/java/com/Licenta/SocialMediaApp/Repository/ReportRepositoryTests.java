package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Content;
import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ReportRepositoryTests {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ContentRepository contentRepository;

    private Post post1;
    private Post post2;

    @BeforeEach
    public void setUp() {
        // Initialize and save Content
        Content content1 = new Content();
        content1.setTextContent("Post 1");
        content1.setFilePath("/filePath");
        contentRepository.save(content1);

        Content content2 = new Content();
        content2.setTextContent("Post 2");
        content2.setFilePath("/filePath");
        contentRepository.save(content2);

        // Initialize and save Posts
        post1 = new Post();
        post1.setContent(content1);
        post1 = postRepository.save(post1);

        post2 = new Post();
        post2.setContent(content2);
        post2 = postRepository.save(post2);

        // Initialize and save Reports
        Report report1 = new Report();
        report1.setReason("Reason 1");
        report1.setPost(post1);
        report1.setReportTime(LocalDateTime.now());
        reportRepository.save(report1);

        Report report2 = new Report();
        report2.setReason("Reason 2");
        report2.setPost(post1);
        report2.setReportTime(LocalDateTime.now());
        reportRepository.save(report2);

        Report report3 = new Report();
        report3.setReason("Reason 3");
        report3.setPost(post2);
        report3.setReportTime(LocalDateTime.now());
        reportRepository.save(report3);
    }

    @Test
    public void testDeleteById() {
        // Given
        Report report = new Report();
        report.setReason("Test Report");
        report = reportRepository.save(report);

        // Ensure the report is saved
        Long reportId = report.getId();
        Optional<Report> savedReport = reportRepository.findById(reportId);
        assertTrue(savedReport.isPresent());

        // When
        reportRepository.deleteById(reportId);

        // Then
        Optional<Report> deletedReport = reportRepository.findById(reportId);
        assertFalse(deletedReport.isPresent());
    }

    @Test
    public void testFindByPostId() {
        // When
        List<Report> reportsForPost1 = reportRepository.findByPostId(post1.getId());
        List<Report> reportsForPost2 = reportRepository.findByPostId(post2.getId());

        // Then
        assertNotNull(reportsForPost1);
        assertEquals(2, reportsForPost1.size());
        assertTrue(reportsForPost1.stream().anyMatch(report -> report.getReason().equals("Reason 1")));
        assertTrue(reportsForPost1.stream().anyMatch(report -> report.getReason().equals("Reason 2")));

        assertNotNull(reportsForPost2);
        assertEquals(1, reportsForPost2.size());
        assertTrue(reportsForPost2.stream().anyMatch(report -> report.getReason().equals("Reason 3")));
    }

    @Test
    public void testFindByPostId_NoReports() {
        // Given
        Long nonExistingPostId = 999L;

        // When
        List<Report> reports = reportRepository.findByPostId(nonExistingPostId);

        // Then
        assertNotNull(reports);
        assertTrue(reports.isEmpty());
    }
}
