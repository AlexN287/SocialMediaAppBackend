package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.Like;

public interface LikeService {
    Like addLike(String jwt, int postId);
    void deleteLike(String jwt, int postId);
    boolean checkUserLikedPost(String jwt, int postId);
}
