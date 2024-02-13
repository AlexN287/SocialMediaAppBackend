package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Config.Security.JwtProvider;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, S3Service s3Service)
    {
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.s3Service=s3Service;
    }
    @Override
    public User findUserByJwt(String jwt) {
        String username = JwtProvider.getUsernameFromJwtToken(jwt);
        User user = userRepository.getUsersByUsername(username);
        return user;
    }
    @Transactional
    @Override
    public void updateUsername(String newUsername, String jwt) throws Exception {
        if (userRepository.existsByUsername(newUsername)) {
            throw new Exception("Username already exists.");
        }

        User user = findUserByJwt(jwt);
        user.setUsername(newUsername);
        userRepository.save(user);
    }
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    @Override
    public List<User> findByUsernameContainingIgnoreCase(String username) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(username);
        users.forEach(user -> user.setPassword(null)); // Set the password to null for each user
        return users;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String jwt) {
        User user = findUserByJwt(jwt);

        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
        {
            throw new BadCredentialsException("Password incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

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

    @Override
    public User findById(int userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found."));
        user.setPassword(null);
        user.setProfileImagePath(null);
        return user;
    }

}
