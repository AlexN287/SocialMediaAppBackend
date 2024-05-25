package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.Report;

import java.util.List;

public interface ReportService {
    Report createReport(Report report);
    void deleteReportById(int reportId, String jwt);
    List<Report> findByPostId(int postId);
}
