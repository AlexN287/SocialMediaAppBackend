package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testGetUsersByUsername() {
        User user = new User("John", "password", "john@email.com", "profileImagePath");
        userRepository.save(user);

        User foundUser = userRepository.getUsersByUsername("John");

        // Then
        assertNotNull(foundUser);
        assertEquals("John", foundUser.getUsername());
    }

    /*@Test
    public void testFindByUsernameContainingIgnoreCase() {
        // Given
        User user1 = new User("john_doe", "password1", "john_doe@example.com", "profileImagePath1");
        User user2 = new User("jane_doe", "password2", "jane_doe@example.com", "profileImagePath2");
        User user3 = new User("doe_john", "password3", "doe_john@example.com", "profileImagePath3");
        User user4 = new User("Marcel", "password4", "marcel@example.com", "profileImagePath4");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        // When
        List<User> foundUsers = userRepository.findByUsernameContainingIgnoreCase("doe");

        // Then
        assertNotNull(foundUsers);
        assertEquals(3, foundUsers.size());
        assertTrue(foundUsers.stream().anyMatch(user -> user.getUsername().equals("john_doe")));
        assertTrue(foundUsers.stream().anyMatch(user -> user.getUsername().equals("jane_doe")));
        assertTrue(foundUsers.stream().anyMatch(user -> user.getUsername().equals("doe_john")));
    }*/

    @Test
    public void testExistsByUsername() {
        // Given
        User user = new User("Marcel", "password", "marcel@example.com", "profileImagePath");
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByUsername("Marcel");

        // Then
        assertTrue(exists);

        // Additional test to check non-existing username
        boolean doesNotExist = userRepository.existsByUsername("NonExistingUsername");
        assertFalse(doesNotExist);
    }

    @Test
    public void testFindById() {
        // Given
        User user = new User("Marcel", "password", "marcel@example.com", "profileImagePath");
        user = userRepository.save(user); // Save and retrieve the persisted entity to get its ID

        // When
        Optional<User> foundUser = userRepository.findById(user.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("Marcel", foundUser.get().getUsername());
        assertEquals("password", foundUser.get().getPassword());
        assertEquals("marcel@example.com", foundUser.get().getEmail());
        assertEquals("profileImagePath", foundUser.get().getProfileImagePath());

        // Additional test to check non-existing ID
        Optional<User> nonExistingUser = userRepository.findById(999L);
        assertFalse(nonExistingUser.isPresent());
    }
}
