package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.Enums.RoleEnum;
import com.Licenta.SocialMediaApp.Model.Role;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.ServiceImpl.ModeratorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModeratorServiceTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private ModeratorServiceImpl moderatorService;  // Use a concrete implementation instead of an interface

    private User moderatorUser;
    private User regularUser;

    @BeforeEach
    public void setUp() {
        // Initialize Roles
        Role moderatorRole = new Role();
        moderatorRole.setRoleName(RoleEnum.MODERATOR);

        Role userRole = new Role();
        userRole.setRoleName(RoleEnum.USER);

        // Initialize Users
        moderatorUser = new User("mod_user", "password", "mod@example.com", "/profile/mod");
        moderatorUser.setId(1L);
        Set<Role> modRoles = new HashSet<>();
        modRoles.add(moderatorRole);
        moderatorUser.setRoles(modRoles);

        regularUser = new User("reg_user", "password", "reg@example.com", "/profile/reg");
        regularUser.setId(2L);
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        regularUser.setRoles(userRoles);
    }

    @Test
    public void testIsModerator_UserIsModerator() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(moderatorUser);

        // When
        boolean isModerator = moderatorService.isModerator(jwt);

        // Then
        assertTrue(isModerator);
        verify(userService, times(1)).findUserByJwt(jwt);
    }

    @Test
    public void testIsModerator_UserIsNotModerator() {
        // Given
        String jwt = "valid.jwt.token";
        when(userService.findUserByJwt(jwt)).thenReturn(regularUser);

        // When
        boolean isModerator = moderatorService.isModerator(jwt);

        // Then
        assertFalse(isModerator);
        verify(userService, times(1)).findUserByJwt(jwt);
    }
}