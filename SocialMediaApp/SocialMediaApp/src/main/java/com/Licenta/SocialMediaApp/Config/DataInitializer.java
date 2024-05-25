package com.Licenta.SocialMediaApp.Config;

import com.Licenta.SocialMediaApp.Model.Enums.RoleEnum;
import com.Licenta.SocialMediaApp.Model.Role;
import com.Licenta.SocialMediaApp.Repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        if (roleRepository.findByRoleName(RoleEnum.ADMIN) == null) {
            roleRepository.save(new Role(RoleEnum.ADMIN));
        }
        if (roleRepository.findByRoleName(RoleEnum.MODERATOR) == null) {
            roleRepository.save(new Role(RoleEnum.MODERATOR));
        }
        if (roleRepository.findByRoleName(RoleEnum.USER) == null) {
            roleRepository.save(new Role(RoleEnum.USER));
        }
    }
}
