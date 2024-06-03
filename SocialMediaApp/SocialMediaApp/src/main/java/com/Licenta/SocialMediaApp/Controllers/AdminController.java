package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.BodyResponse.UserWithRoles;
import com.Licenta.SocialMediaApp.Model.Enums.RoleEnum;
import com.Licenta.SocialMediaApp.Service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/modifyUserRoles/{userId}")
    public ResponseEntity<String> modifyUserRoles(@PathVariable Long userId, @RequestParam List<String> roles, @RequestHeader("Authorization") String jwt) {
        try {
            List<RoleEnum> roleNames = roles.stream()
                    .map(role -> RoleEnum.valueOf(role.toUpperCase()))
                    .collect(Collectors.toList());
            adminService.assignRolesToUser(userId, roleNames, jwt);
            return ResponseEntity.ok("User roles modified successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role name");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/usersWithRoles")
    public ResponseEntity<?> getAllUsersWithRoles(@RequestHeader("Authorization") String jwt,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        try {
            Page<UserWithRoles> usersWithRoles = adminService.getAllUsersWithRoles(jwt, PageRequest.of(page, size));
            return ResponseEntity.ok(usersWithRoles);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode().value()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkIfUserIsAdmin(@RequestHeader("Authorization") String jwt) {
        try {
            boolean isAdmin = adminService.isAdmin(jwt);
            return ResponseEntity.ok(isAdmin);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode().value()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}
