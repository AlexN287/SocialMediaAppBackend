package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.BlockList;
import com.Licenta.SocialMediaApp.Model.BlockListId;
import com.Licenta.SocialMediaApp.Model.User;
import org.junit.jupiter.api.BeforeEach;
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
public class BlockListRepositoryTests {

    @Autowired
    private BlockListRepository blockListRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {
        // Initialize and save Users
        user1 = new User("john_doe", "password123", "john_doe@example.com", "/profile/path1");
        user1 = userRepository.save(user1);

        user2 = new User("jane_doe", "password456", "jane_doe@example.com", "/profile/path2");
        user2 = userRepository.save(user2);

        user3 = new User("alice_smith", "password789", "alice_smith@example.com", "/profile/path3");
        user3 = userRepository.save(user3);

        // Initialize and save BlockList
        BlockList blockList1 = new BlockList();
        blockList1.setId(new BlockListId(user1, user2));
        blockListRepository.save(blockList1);

        BlockList blockList2 = new BlockList();
        blockList2.setId(new BlockListId(user1, user3));
        blockListRepository.save(blockList2);
    }

    @Test
    public void testFindByUserAndBlockedUser() {
        // When
        Optional<BlockList> blockList = blockListRepository.findByUserAndBlockedUser(user1.getId(), user2.getId());

        // Then
        assertTrue(blockList.isPresent());
        assertEquals(user1.getId(), blockList.get().getId().getUser().getId());
        assertEquals(user2.getId(), blockList.get().getId().getBlockedUser().getId());
    }

    @Test
    public void testFindBlockedUsersByUserId() {
        // When
        List<User> blockedUsers = blockListRepository.findBlockedUsersByUserId(user1.getId());

        // Then
        assertNotNull(blockedUsers);
        assertEquals(2, blockedUsers.size());
        assertTrue(blockedUsers.contains(user2));
        assertTrue(blockedUsers.contains(user3));
    }

    @Test
    public void testExistsByBlockedUserAndUser() {
        // When
        boolean exists = blockListRepository.existsByBlockedUserAndUser(user1.getId(), user2.getId());

        // Then
        assertTrue(exists);
    }
}
