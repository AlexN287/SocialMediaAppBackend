package com.Licenta.SocialMediaApp.Service.ServiceImpl;

import com.Licenta.SocialMediaApp.Model.BlockList;
import com.Licenta.SocialMediaApp.Model.BlockListId;
import com.Licenta.SocialMediaApp.Model.FriendsList;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.BlockListRepository;
import com.Licenta.SocialMediaApp.Repository.FriendsListRepository;
import com.Licenta.SocialMediaApp.Service.BlockListService;
import com.Licenta.SocialMediaApp.Service.FriendsListService;
import com.Licenta.SocialMediaApp.Service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BlockListServiceImpl implements BlockListService {
    private final UserService userService;
    private final BlockListRepository blockListRepository;

    private final FriendsListRepository friendsListRepository;
    private final FriendsListService friendsListService;

    public BlockListServiceImpl(UserService userService, BlockListRepository blockListRepository, FriendsListRepository friendsListRepository, FriendsListService friendsListService) {
        this.userService = userService;
        this.blockListRepository = blockListRepository;
        this.friendsListRepository = friendsListRepository;
        this.friendsListService = friendsListService;
    }
    @Override
    @Transactional
    public boolean blockUser(String jwt, int blockedUserId) {
        User loggedUser = userService.findUserByJwt(jwt);

        Optional<BlockList> existingBlock = blockListRepository.findByUserAndBlockedUser(loggedUser.getId(), blockedUserId);
        if (existingBlock.isPresent()) {
            // Return false if the block already exists
            return false;
        }

        Optional<FriendsList> friendsList = friendsListRepository.findByUsers(loggedUser.getId(), blockedUserId);
        if (friendsList.isPresent()) {
            friendsListService.deleteFriend(jwt, blockedUserId);
        }

        BlockList newBlock = new BlockList();
        User blockedUser = new User(); // Similarly for the blocked user
        blockedUser.setId(blockedUserId);

        BlockListId id = new BlockListId();
        id.setUser(loggedUser);
        id.setBlockedUser(blockedUser);
        newBlock.setId(id);

        blockListRepository.save(newBlock);
        return true;
    }

    @Override
    public boolean unblockUser(String jwt, int blockedUserId) {
        User loggedUser = userService.findUserByJwt(jwt);

        Optional<BlockList> block = blockListRepository.findByUserAndBlockedUser(loggedUser.getId(), blockedUserId);
        if (block.isPresent()) {
            // If the block exists, delete it
            blockListRepository.delete(block.get());
            return true;
        } else {
            // If the block does not exist, indicate so with false
            return false;
        }
    }

    @Override
    public List<User> getBlockedUsersByUserId(int userId) {
        return blockListRepository.findBlockedUsersByUserId(userId);
    }

    @Override
    public boolean isUserBlockedBy(String jwt, int otherUserId) {
        User loggedUser = userService.findUserByJwt(jwt);
        return blockListRepository.existsByBlockedUserAndUser(otherUserId, loggedUser.getId());
    }
}
