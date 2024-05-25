package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Model.Report;
import com.Licenta.SocialMediaApp.Repository.ReportRepository;
import com.Licenta.SocialMediaApp.Service.ModeratorService;
import com.Licenta.SocialMediaApp.Service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ModeratorService moderatorService;

    public ReportServiceImpl(ReportRepository reportRepository, ModeratorService moderatorService) {
        this.reportRepository = reportRepository;
        this.moderatorService = moderatorService;
    }
    @Override
    public Report createReport(Report report) {
        report.setReportTime(LocalDateTime.now());
        return reportRepository.save(report);
    }
    @Override
    public void deleteReportById(int reportId, String jwt) {
        if (!moderatorService.isModerator(jwt)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
        }

        reportRepository.deleteById(reportId);
    }

    @Override
    public List<Report> findByPostId(int postId) {
        return reportRepository.findByPostId(postId);
    }
}
