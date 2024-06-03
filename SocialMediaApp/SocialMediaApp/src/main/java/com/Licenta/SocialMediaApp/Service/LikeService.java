package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.Like;

public interface LikeService {
    Like addLike(String jwt, Long postId);
    void deleteLike(String jwt, Long postId);
    boolean checkUserLikedPost(String jwt, Long postId);
}
