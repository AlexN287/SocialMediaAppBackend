package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserWithRoles;
import com.Licenta.SocialMediaApp.Model.Enums.RoleEnum;
import com.Licenta.SocialMediaApp.Model.Role;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.RoleRepository;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.AdminService;
import com.Licenta.SocialMediaApp.Service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    public AdminServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }
    @Override
    public boolean isAdmin(String jwt) {
        User user = userService.findUserByJwt(jwt);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == RoleEnum.ADMIN);
    }
    @Transactional
    public void assignRolesToUser(Long userId, List<RoleEnum> roleNames, String jwt) {

        if (!isAdmin(jwt)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Clear existing roles
        user.getRoles().clear();

        for (RoleEnum roleName : roleNames) {
            Role role = roleRepository.findByRoleName(roleName);
            if (role != null) {
                user.getRoles().add(role);
            } else {
                throw new RuntimeException("Role " + roleName + " not found");
            }
        }
        userRepository.save(user);
    }

    @Override
    public Page<UserWithRoles> getAllUsersWithRoles(String jwt, Pageable pageable) {
        if (!isAdmin(jwt)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
        }

        Page<User> usersPage = userRepository.findAll(pageable);
        List<UserWithRoles> usersWithRoles = usersPage.stream()
                .map(user -> new UserWithRoles(
                        user.getId(),
                        user.getUsername(),
                        user.getRoles().stream()
                                .map(role -> role.getRoleName().name())
                                .collect(Collectors.toSet()),
                        user.getEmail(),
                        user.getProfileImagePath()
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(usersWithRoles, pageable, usersPage.getTotalElements());
    }


}
