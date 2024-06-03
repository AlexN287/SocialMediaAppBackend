package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserWithRoles;
import com.Licenta.SocialMediaApp.Model.Enums.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    boolean isAdmin(String jwt);
    void assignRolesToUser(Long userId, List<RoleEnum> roleNames, String jwt);
    Page<UserWithRoles> getAllUsersWithRoles(String jwt, Pageable pageable);
}
