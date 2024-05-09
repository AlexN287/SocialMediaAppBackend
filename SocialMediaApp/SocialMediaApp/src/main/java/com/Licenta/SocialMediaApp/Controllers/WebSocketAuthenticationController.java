package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Config.Security.JwtProvider;
import com.Licenta.SocialMediaApp.Config.Security.JwtValidator;
import com.Licenta.SocialMediaApp.Config.Security.UserDetailsServiceImpl;
import com.Licenta.SocialMediaApp.Model.Authentication.TokenMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebSocketAuthenticationController {

    @Autowired
    private JwtProvider jwtProvider; // Your JWT utility class

    @Autowired
    UserDetailsServiceImpl customUserDetailsService;

    @Autowired
    private JwtValidator jwtValidator;

    /*@MessageMapping("/verifyToken")
    public void verifyToken(@Payload TokenMessage tokenMessage, SimpMessageHeaderAccessor headerAccessor) {
        String token = tokenMessage.getToken();
        try {
            // Ensure token is properly formatted
            if (!token.startsWith("Bearer ")) {
                System.out.println("Invalid token format.");
                return;
            }

            if (jwtTokenProvider.validateToken(token)) {
                // Authentication successful, extract username and create Authentication object
                String username = JwtProvider.getUsernameFromJwtToken(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Set authentication in security context and session
                SecurityContextHolder.getContext().setAuthentication(auth);
                headerAccessor.getSessionAttributes().put("user", auth.getPrincipal());
                System.out.println("Token validated successfully. User: " + username);
            } else {
                System.out.println("Invalid token.");
            }
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
        }
    }*/

    @MessageMapping("/verifyToken")
    public void verifyToken(@Payload TokenMessage tokenMessage, SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        String token = tokenMessage.getToken();
        try {
            System.out.println("Controller: " + token);
            if (jwtProvider.validateToken(token)) {
                System.out.println("Controller");
                Authentication auth = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                // Optionally store the authentication in session attributes
                headerAccessor.getSessionAttributes().put("authentication", auth);
                System.out.println("Token validated successfully, user: " + auth.getName());
            } else {
                System.out.println("Invalid token.");
            }
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            // Consider closing the WebSocket session if authentication fails
        }
    }

}

