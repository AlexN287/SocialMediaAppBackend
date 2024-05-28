package com.Licenta.SocialMediaApp.Controllers;


import com.Licenta.SocialMediaApp.Config.Security.JwtProvider;
import com.Licenta.SocialMediaApp.Config.Security.UserDetailsServiceImpl;
import com.Licenta.SocialMediaApp.Model.Authentication.AuthResponse;
import com.Licenta.SocialMediaApp.Model.Authentication.LoginRequest;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.AuthenticationService;
import com.Licenta.SocialMediaApp.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
    @PostMapping("/signup")
    public AuthResponse createUser(@RequestPart User user, @RequestPart MultipartFile profileImage) throws Exception {
        authenticationService.registerUser(user, profileImage);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        String token = JwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse(token, "Register Succesfull");

        return authResponse;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest)
    {
        try{
            Authentication authentication = authenticationService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            String token = JwtProvider.generateToken(authentication);

            AuthResponse authResponse = new AuthResponse(token, "Login succesfull");

            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().body("You have been logged out successfully.");
    }
}