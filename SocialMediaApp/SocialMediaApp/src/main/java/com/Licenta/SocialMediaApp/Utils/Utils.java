package com.Licenta.SocialMediaApp.Utils;

import com.Licenta.SocialMediaApp.Model.BodyResponse.CommentResponse;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.Comment;
import com.Licenta.SocialMediaApp.Model.User;

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
}
