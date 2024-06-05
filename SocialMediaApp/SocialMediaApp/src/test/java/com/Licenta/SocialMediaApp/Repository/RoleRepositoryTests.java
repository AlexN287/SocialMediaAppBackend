package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.Enums.RoleEnum;
import com.Licenta.SocialMediaApp.Model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testFindByRoleName() {
        // Given
        Role roleUser = new Role();
        roleUser.setRoleName(RoleEnum.USER);
        roleRepository.save(roleUser);

        Role roleAdmin = new Role();
        roleAdmin.setRoleName(RoleEnum.ADMIN);
        roleRepository.save(roleAdmin);

        // When
        Role foundRoleUser = roleRepository.findByRoleName(RoleEnum.USER);
        Role foundRoleAdmin = roleRepository.findByRoleName(RoleEnum.ADMIN);

        // Then
        assertNotNull(foundRoleUser);
        assertEquals(RoleEnum.USER, foundRoleUser.getRoleName());

        assertNotNull(foundRoleAdmin);
        assertEquals(RoleEnum.ADMIN, foundRoleAdmin.getRoleName());
    }

}