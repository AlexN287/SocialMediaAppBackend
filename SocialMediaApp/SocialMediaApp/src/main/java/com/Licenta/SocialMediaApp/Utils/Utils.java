package com.Licenta.SocialMediaApp.Utils;

import com.Licenta.SocialMediaApp.Model.*;
import com.Licenta.SocialMediaApp.Model.BodyResponse.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {
    public static UserResponse convertToUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getProfileImagePath());
    }
    public static CommentResponse convertToCommentResponse(Comment comment) {
        UserResponse userResponse = convertToUserResponse(comment.getUser());
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getTimestamp(),
                userResponse
        );
    }

    public static String getFileExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return ""; // Handle null and empty string
        }

        int lastIndex = originalFilename.lastIndexOf('.');
        if (lastIndex == -1 || lastIndex == originalFilename.length() - 1) {
            return ""; // No extension or the filename ends with a dot
        }

        return originalFilename.substring(lastIndex);
    }

    public static PostResponse convertToPostResponse(Post post) {
        UserResponse userResponse = convertToUserResponse(post.getUser());
        return new PostResponse(
                post.getId(),
                userResponse,
                post.getContent(),
                post.getCreatedAt()
        );
    }

    public static MessageResponse convertToMessageResponse(Message message) {
        UserResponse senderResponse = convertToUserResponse(message.getSender());
        return new MessageResponse(
                message.getId(),
                message.getConversation(),
                senderResponse,
                message.getContent(),
                message.getTimestamp(),
                message.getMessageType()
        );
    }
    public static LikeResponse convertToLikeResponse(Like like) {
        UserResponse userResponse = convertToUserResponse(like.getUser());
        PostResponse postResponse = convertToPostResponse(like.getPost());
        return new LikeResponse(
                like.getId(),
                userResponse,
                postResponse
        );
    }

    public static UserWithRoles convertToUserWithRoles(User user) {
        UserWithRoles userWithRoles = new UserWithRoles();
        userWithRoles.setId(user.getId());
        userWithRoles.setUsername(user.getUsername());
        userWithRoles.setEmail(user.getEmail());
        userWithRoles.setProfileImagePath(user.getProfileImagePath());

        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name()) // Convert enum to string
                .collect(Collectors.toSet());
        userWithRoles.setRoles(roles);

        return userWithRoles;
    }

    public static ReportResponse convertToReportResponse(Report report) {
        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setId(report.getId());
        reportResponse.setUser(convertToUserResponse(report.getUser()));
        reportResponse.setReason(report.getReason());
        reportResponse.setReportTime(report.getReportTime());
        return reportResponse;
    }


    public static PostWithReports convertToPostWithReports(Post post) {
        PostWithReports postWithReports = new PostWithReports();
        postWithReports.setId(post.getId());
        postWithReports.setUser(convertToUserResponse(post.getUser()));
        postWithReports.setContent(post.getContent());
        postWithReports.setCreatedAt(post.getCreatedAt());
        List<ReportResponse> reportResponses = post.getReports().stream()
                .map(Utils::convertToReportResponse)
                .collect(Collectors.toList());
        postWithReports.setReports(reportResponses); // Assuming Post entity has getReports() method

        return postWithReports;
    }


}
