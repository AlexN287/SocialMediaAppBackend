package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Model.BodyResponse.CommentResponse;
import com.Licenta.SocialMediaApp.Model.Comment;
import com.Licenta.SocialMediaApp.Model.Content;
import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.CommentRepository;
import com.Licenta.SocialMediaApp.Repository.ContentRepository;
import com.Licenta.SocialMediaApp.Repository.PostRepository;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.CommentService;
import com.Licenta.SocialMediaApp.Service.UserService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ContentRepository contentRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, ContentRepository contentRepository, PostRepository postRepository, UserService userService, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.contentRepository = contentRepository;
        this.postRepository = postRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public Comment addComment(String jwt, Long postId, String commentText) {
        User loggedUser = userService.findUserByJwt(jwt);

        if (loggedUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Content content = new Content();
        content.setTextContent(commentText);
        content = contentRepository.save(content);

        Comment comment = new Comment();
        comment.setUser(loggedUser);
        comment.setPost(post);
        comment.setContent(content);
        comment.setTimestamp(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId, String jwt) {
        User loggedUser = userService.findUserByJwt(jwt);

        if (loggedUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // Check if the user is the creator of the comment or an admin
        if (comment.getUser().getId()!=loggedUser.getId()) {
            throw new IllegalStateException("Unauthorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
    @Override
    public long getCommentCountForPost(Long postId) {
        return commentRepository.countByPostId(postId);
    }
    @Override
    public List<CommentResponse> getCommentsForPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(Utils::convertToCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Comment updateCommentText(Long commentId, String newText, String jwt) {
        User user = userService.findUserByJwt(jwt);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (comment.getUser().getId() != user.getId()) {
            throw new IllegalStateException("Unauthorized to update this comment");
        }

        Content content = comment.getContent();
        content.setTextContent(newText);
        contentRepository.save(content);
        return commentRepository.save(comment);
    }
}
