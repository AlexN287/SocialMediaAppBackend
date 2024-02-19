package com.Licenta.SocialMediaApp.Controllers;


import com.Licenta.SocialMediaApp.Config.Security.JwtProvider;
import com.Licenta.SocialMediaApp.Config.Security.UserDetailsServiceImpl;
import com.Licenta.SocialMediaApp.Model.Authentication.AuthResponse;
import com.Licenta.SocialMediaApp.Model.Authentication.LoginRequest;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserService userService;

    public AuthController(UserService userService)
    {
        this.userService=userService;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/signup")
    public AuthResponse createUser(@RequestPart User user, @RequestPart MultipartFile profileImage) throws Exception {
        userService.registerUser(user, profileImage);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        String token = JwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse(token, "Register Succesfull");

        return authResponse;
    }

    @PostMapping("/signin")
    public AuthResponse signIn(@RequestBody LoginRequest loginRequest)
    {
        Authentication authentication = authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        String token = JwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse(token, "Login succesfull");

        return authResponse;
    }

    private Authentication authenticate(String username,String password)
    {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if(userDetails==null)
        {
            throw new BadCredentialsException("Invalid username");
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword()))
        {
            throw new BadCredentialsException("Password incorrect");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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