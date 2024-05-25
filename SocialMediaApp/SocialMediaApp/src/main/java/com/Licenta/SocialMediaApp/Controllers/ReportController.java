package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.Report;
import com.Licenta.SocialMediaApp.Service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody Report report) {
        Report savedReport = reportService.createReport(report);
        return ResponseEntity.ok(savedReport);
    }

    @DeleteMapping("/delete/{reportId}")
    public ResponseEntity<?> deleteReport(@PathVariable int reportId, @RequestHeader("Authorization") String jwt) {
        try {
            reportService.deleteReportById(reportId, jwt);
            return ResponseEntity.ok("Report deleted successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode().value()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/reports/{postId}")
    public ResponseEntity<List<Report>> getReportsByPostId(@PathVariable int postId) {
        List<Report> reports = reportService.findByPostId(postId);
        return ResponseEntity.ok(reports);
    }

}
