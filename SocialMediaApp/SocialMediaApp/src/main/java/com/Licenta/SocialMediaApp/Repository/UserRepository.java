package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = :username")
    User getUsersByUsername(@Param("username") String username);
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')) AND u.id <> :loggedInUserId")
    Page<User> searchByUsernameExcludingLoggedInUser(@Param("username") String username, @Param("loggedInUserId") Long loggedInUserId, Pageable pageable);
    boolean existsByUsername(String username);
    Optional<User> findById(Long userId);

}
