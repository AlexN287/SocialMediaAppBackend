package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.User;

import java.util.List;

public interface BlockListService {
    boolean blockUser(String jwt, int blockedUserId);
    boolean unblockUser(String jwt, int blockedUserId);
    List<User> getBlockedUsersByUserId(int userId);
}
