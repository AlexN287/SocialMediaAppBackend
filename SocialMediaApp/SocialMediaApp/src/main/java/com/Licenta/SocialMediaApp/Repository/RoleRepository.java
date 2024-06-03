package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Enums.RoleEnum;
import com.Licenta.SocialMediaApp.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(RoleEnum roleName);
    List<Role> findRoleByRoleNameIn(List<RoleEnum> names);
}
