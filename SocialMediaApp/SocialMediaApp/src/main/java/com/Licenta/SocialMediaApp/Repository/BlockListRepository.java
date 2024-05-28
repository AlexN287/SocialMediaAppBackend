package com.Licenta.SocialMediaApp.Repository;

import com.Licenta.SocialMediaApp.Model.BlockList;
import com.Licenta.SocialMediaApp.Model.BlockListId;
import com.Licenta.SocialMediaApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlockListRepository extends JpaRepository<BlockList, BlockListId> {
    @Query("SELECT bl FROM BlockList bl WHERE " +
            "bl.id.user.id = :userId AND bl.id.blockedUser.id = :blockedUserId")
    Optional<BlockList> findByUserAndBlockedUser(@Param("userId") int userId, @Param("blockedUserId") int blockedUserId);

    @Query("SELECT bl.id.blockedUser FROM BlockList bl WHERE bl.id.user.id = :userId")
    List<User> findBlockedUsersByUserId(@Param("userId") int userId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BlockList b WHERE b.id.blockedUser.id = :blockedUserId AND b.id.user.id = :userId")
    boolean existsByBlockedUserAndUser(@Param("userId") int userId, @Param("blockedUserId") int blockedUserId);
}
