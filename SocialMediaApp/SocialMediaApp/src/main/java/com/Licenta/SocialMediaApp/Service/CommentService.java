package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.BodyResponse.CommentResponse;
import com.Licenta.SocialMediaApp.Model.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(String jwt, int postId, String commentText);
    void deleteComment(int commentId, String jwt);
    long getCommentCountForPost(int postId);
    List<CommentResponse> getCommentsForPost(int postId);
    Comment updateCommentText(int commentId, String newText, String jwt);
}
