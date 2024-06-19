package com.Licenta.SocialMediaApp.Controller;

import com.Licenta.SocialMediaApp.Controllers.UserController;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.UserService;
import com.Licenta.SocialMediaApp.Utils.Utils;

import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;



@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetUserFromToken_Success() throws Exception {
        // Given
        String jwt = "valid.jwt.token";
        User user = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");
        UserResponse userResponse = new UserResponse("john_doe", "john_doe@example.com", "profileImagePath1");

        when(userService.findUserByJwt(jwt)).thenReturn(user);
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.convertToUserResponse(user)).thenReturn(userResponse);

            // When / Then
            mockMvc.perform(get("/user/profile")
                            .header("Authorization", jwt)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("john_doe"))
                    .andExpect(jsonPath("$.email").value("john_doe@example.com"))
                    .andExpect(jsonPath("$.profileImagePath").value("profileImagePath1"));
        }
    }

    @Test
    public void testGetUserFromToken_UserNotFound() throws Exception {
        // Given
        String jwt = "valid.jwt.token";

        when(userService.findUserByJwt(jwt)).thenThrow(new EntityNotFoundException("User not found"));

        // When / Then
        mockMvc.perform(get("/user/profile")
                        .header("Authorization", jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserFromToken_InternalServerError() throws Exception {
        // Given
        String jwt = "valid.jwt.token";

        when(userService.findUserByJwt(jwt)).thenThrow(new RuntimeException("Internal server error"));

        // When / Then
        mockMvc.perform(get("/user/profile")
                        .header("Authorization", jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
