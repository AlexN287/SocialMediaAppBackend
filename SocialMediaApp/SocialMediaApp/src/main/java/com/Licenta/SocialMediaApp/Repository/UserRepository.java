package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.username = :username")
    User getUsersByUsername(@Param("username") String username);
    List<User> findByUsernameContainingIgnoreCase(String username);
    boolean existsByUsername(String username);
}
