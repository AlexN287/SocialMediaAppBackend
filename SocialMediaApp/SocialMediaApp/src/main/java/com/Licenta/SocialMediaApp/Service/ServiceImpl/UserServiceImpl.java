package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Config.Security.JwtProvider;
import com.Licenta.SocialMediaApp.Model.BodyResponse.UserResponse;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.FriendsListRepository;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.UserService;
import com.Licenta.SocialMediaApp.Utils.Utils;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final FriendsListRepository friendsListRepository;
    private final SimpUserRegistry simpUserRegistry;
    private final Cache<Integer, byte[]> profileImageCache;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, S3Service s3Service, FriendsListRepository friendsListRepository, SimpUserRegistry simpUserRegistry, Cache<Integer, byte[]> profileImageCache)
    {
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.s3Service=s3Service;
        this.friendsListRepository = friendsListRepository;
        this.simpUserRegistry = simpUserRegistry;
        this.profileImageCache = profileImageCache;
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
    public byte[] getUserProfileImage(int userId, String jwt) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        byte[] cachedImage = profileImageCache.getIfPresent(userId);
        if (cachedImage != null) {
            return cachedImage;
        }

        if (user.getProfileImagePath() == null || user.getProfileImagePath().isEmpty()) {
            throw new Exception("No profile image set for user");
        }

        byte[] imageBytes = s3Service.getObject(user.getProfileImagePath());

        // Put the image in the cache

        User loggedUser = findUserByJwt(jwt);

        if(loggedUser.getId()==userId || friendsListRepository.isFriendshipExists(loggedUser.getId(), userId)){
            profileImageCache.put(userId, imageBytes);
        }

        return imageBytes;
    }



    @Override
    public List<UserResponse> getConnectedFriends(String jwt) {
        User loggedUser = findUserByJwt(jwt);

        List<User> friends = friendsListRepository.findFriendsByUserId(loggedUser.getId());
        Set<String> connectedUsernames = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName)
                .collect(Collectors.toSet());

        System.out.println(connectedUsernames.size());
        for (String username : connectedUsernames) {
            System.out.println(username);
        }
        return friends.stream()
                .filter(friend -> connectedUsernames.contains(friend.getUsername()))
                .map(Utils::convertToUserResponse)
                .collect(Collectors.toList());
    }


}
