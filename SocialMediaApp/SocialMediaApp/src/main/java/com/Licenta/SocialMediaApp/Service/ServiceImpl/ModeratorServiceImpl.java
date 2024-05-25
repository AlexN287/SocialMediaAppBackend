package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Model.Enums.RoleEnum;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Service.ModeratorService;
import com.Licenta.SocialMediaApp.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ModeratorServiceImpl implements ModeratorService {

    private final UserService userService;

    public ModeratorServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isModerator(String jwt) {
        User user = userService.findUserByJwt(jwt);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == RoleEnum.MODERATOR);
    }
}
