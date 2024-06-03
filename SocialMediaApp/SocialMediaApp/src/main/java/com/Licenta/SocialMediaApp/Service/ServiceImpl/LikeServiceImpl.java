package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Model.Like;
import com.Licenta.SocialMediaApp.Model.Post;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.LikeRepository;
import com.Licenta.SocialMediaApp.Repository.PostRepository;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.LikeService;
import com.Licenta.SocialMediaApp.Service.UserService;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeServiceImpl(LikeRepository likeRepository, UserService userService, PostRepository postRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Like addLike(String jwt, Long postId) {
        User loggedUser = userService.findUserByJwt(jwt);

        if (loggedUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (likeRepository.existsByUserAndPost(loggedUser, post)) {
            throw new IllegalStateException("User already liked this post");
        }

        Like like = new Like();
        like.setUser(loggedUser);
        like.setPost(post);
        return likeRepository.save(like);
    }
    @Override
    public void deleteLike(String jwt, Long postId) {
        User loggedUser = userService.findUserByJwt(jwt);

        if (loggedUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Like like = likeRepository.findByUserAndPost(loggedUser, post);
        if (like == null) {
            throw new IllegalStateException("Like not found");
        }

        likeRepository.delete(like);
    }

    @Override
    public boolean checkUserLikedPost(String jwt, Long postId) {

        User loggedUser = userService.findUserByJwt(jwt);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return likeRepository.existsByUserAndPost(loggedUser, post);
    }
}
