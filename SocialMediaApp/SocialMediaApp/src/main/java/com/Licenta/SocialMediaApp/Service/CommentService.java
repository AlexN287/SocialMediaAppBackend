package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.BodyResponse.CommentResponse;
import com.Licenta.SocialMediaApp.Model.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(String jwt, Long postId, String commentText);
    void deleteComment(Long commentId, String jwt);
    long getCommentCountForPost(Long postId);
    List<CommentResponse> getCommentsForPost(Long postId);
    Comment updateCommentText(Long commentId, String newText, String jwt);
}
