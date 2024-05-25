package com.Licenta.SocialMediaApp.Model.BodyResponse;

import com.Licenta.SocialMediaApp.Model.Content;
import com.Licenta.SocialMediaApp.Model.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostWithReports {
    private int id;
    private UserResponse user;
    private Content content;
    private LocalDateTime createdAt;
    private List<ReportResponse> reports;
}
