package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Config.Security.JwtProvider;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    public String updateUsername(String newUsername, String jwt) throws Exception {
        if (userRepository.existsByUsername(newUsername)) {
            throw new Exception("Username already exists.");
        }

        User user = findUserByJwt(jwt);

        user.setUsername(newUsername);
        userRepository.save(user);

        String newJwt = JwtProvider.generateToken(newUsername);
        return  newJwt;
    }
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    @Override
    public List<User> findByUsernameContainingIgnoreCase(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
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
    @Override
    public User findById(int userId) throws EntityNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }


    @Override
    public void uploadUserProfileImage(int userId, MultipartFile file) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        String profileImageKey = s3Service.generateProfileImageKey(userId, file);
        s3Service.putObject(profileImageKey, file.getBytes());

        user.setProfileImagePath(profileImageKey);
        userRepository.save(user);
    }

    @Override
    @Cacheable(value = "profileImages", key = "#userId")
    public byte[] getUserProfileImage(int userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (user.getProfileImagePath() == null || user.getProfileImagePath().isEmpty()) {
            throw new Exception("No profile image set for user");
        }

        return s3Service.getObject(user.getProfileImagePath());
    }

}
