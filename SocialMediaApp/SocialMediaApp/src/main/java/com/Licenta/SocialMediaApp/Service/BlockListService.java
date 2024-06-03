package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.User;

import java.util.List;

public interface BlockListService {
    boolean blockUser(String jwt, Long blockedUserId);
    boolean unblockUser(String jwt, Long blockedUserId);
    List<User> getBlockedUsersByUserId(Long userId);
    boolean isUserBlockedBy(String jwt, Long otherUserId);
}
