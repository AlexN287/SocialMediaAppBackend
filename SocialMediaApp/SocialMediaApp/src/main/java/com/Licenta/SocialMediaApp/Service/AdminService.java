package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserWithRoles;
import com.Licenta.SocialMediaApp.Model.Enums.RoleEnum;

import java.util.List;

public interface AdminService {
    boolean isAdmin(String jwt);
    void assignRolesToUser(Integer userId, List<RoleEnum> roleNames, String jwt);
    List<UserWithRoles> getAllUsersWithRoles(String jwt);
}
