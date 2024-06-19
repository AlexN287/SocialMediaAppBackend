package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Config.Security.JwtProvider;
import com.Licenta.SocialMediaApp.Model.Report;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.ReportRepository;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.ServiceImpl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTests {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModeratorService moderatorService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Report report;
    private User user;

    @BeforeEach
    public void setUp() {
        // Initialize and mock User
        user = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user.setId(1L);

        // Initialize and mock Report
        report = new Report();
        report.setId(1L);
        report.setReason("Test Report");
        report.setUser(user);

        lenient().when(reportRepository.save(any(Report.class))).thenReturn(report);
        lenient().when(reportRepository.findById(anyLong())).thenReturn(Optional.of(report));
        lenient().when(reportRepository.findByPostId(anyLong())).thenReturn(Collections.singletonList(report));
        lenient().when(userRepository.getUsersByUsername(anyString())).thenReturn(user);
        lenient().when(moderatorService.isModerator(anyString())).thenReturn(true);

    }

    @Test
    public void testCreateReport() {
        // When
        Report createdReport = reportService.createReport(report);

        // Then
        assertNotNull(createdReport);
        assertEquals(report.getId(), createdReport.getId());
        verify(reportRepository, times(1)).save(report);
    }

    @Test
    public void testDeleteReportById() {
        // Given
        String jwt = "valid.jwt.token";

        try (MockedStatic<JwtProvider> mockedJwt = mockStatic(JwtProvider.class)) {
            mockedJwt.when(() -> JwtProvider.getUsernameFromJwtToken(anyString())).thenReturn("john_doe");

            // When
            reportService.deleteReportById(report.getId(), jwt);

            // Then
            verify(reportRepository, times(1)).deleteById(report.getId());
        }
    }

    @Test
    public void testFindByPostId() {
        // When
        List<Report> reports = reportService.findByPostId(1L);

        // Then
        assertNotNull(reports);
        assertEquals(1, reports.size());
        assertEquals(report.getId(), reports.get(0).getId());
        verify(reportRepository, times(1)).findByPostId(1L);
    }

    @Test
    public void testDeleteReportById_Unauthorized() {
        // Given
        String jwt = "invalid.jwt.token";

        // Mock the moderator check to return false
        when(moderatorService.isModerator(anyString())).thenReturn(false);

        // When / Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reportService.deleteReportById(report.getId(), jwt);
        });

        assertEquals("403 FORBIDDEN \"You do not have permission to perform this action\"", exception.getMessage());
        verify(reportRepository, times(0)).deleteById(report.getId());
    }
}