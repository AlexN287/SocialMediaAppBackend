package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Config.Security.JwtProvider;
import com.Licenta.SocialMediaApp.Config.Security.UserDetailsServiceImpl;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.AuthenticationService;
import com.Licenta.SocialMediaApp.Service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    public AuthenticationServiceImpl(S3Service s3Service, PasswordEncoder passwordEncoder,
                                     UserDetailsServiceImpl userDetailsService, UserRepository userRepository) {
        this.s3Service = s3Service;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(String username,String password)
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

    @Transactional
    @Override
    public void registerUser(User user, MultipartFile profileImage) throws Exception {
        User isExist = userRepository.getUsersByUsername(user.getUsername());

        if(isExist!=null)
        {
            throw new Exception("Username already exists");
        }

        User newUser = new User();

        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setProfileImagePath("/path");

        User savedUser = userRepository.save(newUser);

        String profileImageKey = s3Service.generateProfileImageKey(savedUser.getId(), profileImage);
        s3Service.putObject(profileImageKey, profileImage.getBytes());

        newUser.setProfileImagePath(profileImageKey);
        userRepository.save(newUser);
    }

}
