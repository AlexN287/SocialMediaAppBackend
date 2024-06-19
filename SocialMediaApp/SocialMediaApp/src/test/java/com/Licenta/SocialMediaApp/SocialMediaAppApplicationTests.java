package com.Licenta.SocialMediaApp;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.FriendsListRepository;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import com.Licenta.SocialMediaApp.Service.UserService;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
@SpringBootTest
@ActiveProfiles("test")
class SocialMediaAppApplicationTests {

	/*@SpyBean
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private S3Service s3Service;

	@Autowired
	private Cache<Integer, byte[]> profileImageCache;

	@MockBean
	private FriendsListRepository friendsListRepository;*/

	@Test
	public void contextLoads() {
		//Assertions.assertNotNull(profileImageCache);
	}

	@Autowired
	private UserService userService;

	@Autowired
	private Cache<Integer, byte[]> profileImageCache;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private S3Service s3Service;

	/*@Test
	public void testGetUserProfileImage_CacheFunctionality() throws Exception {
		int userId = 1;
		byte[] imageBytes = "imageData".getBytes();
		String profileImagePath = "users/10/profile/profileImage.png";
		String jwtToken = "mockJwtToken";
		User loggedInUser = new User();
		loggedInUser.setId(userId);

		User user = new User();
		user.setId(userId);
		user.setProfileImagePath(profileImagePath);

		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		Mockito.when(s3Service.getObject(profileImagePath)).thenReturn(imageBytes);
		Mockito.doReturn(loggedInUser).when(userService).findUserByJwt(jwtToken);
		Mockito.when(friendsListRepository.isFriendshipExists(loggedInUser.getId(), userId)).thenReturn(true);

		// First call should fetch from the service and put in the cache
		byte[] result = userService.getUserProfileImage(userId, jwtToken);
		Assertions.assertArrayEquals(imageBytes, result);

		// Cache should contain the image now
		byte[] cachedImage = profileImageCache.getIfPresent(userId);
		System.out.println("Cached Image After First Fetch: " + cachedImage); // Debugging line
		Assertions.assertNotNull(cachedImage);
		Assertions.assertArrayEquals(imageBytes, cachedImage);

		// Wait for the cache to expire (adjust the sleep time according to your cache configuration)
		Thread.sleep(10500); // Wait for 1.5 seconds to ensure the cache expires

		// Cache should be empty now
		cachedImage = profileImageCache.getIfPresent(userId);
		System.out.println("Cached Image After Expiration: " + cachedImage); // Debugging line
		Assertions.assertNull(cachedImage);
	}*/

	/*@Test
	public void testGetUserProfileImage_CacheFunctionality() throws Exception {
		int userId = 10;
		byte[] imageBytes = "imageData".getBytes();
		String profileImagePath = "users/10/profile/profileImage.png";

		User user = new User();
		user.setId(userId);
		user.setProfileImagePath(profileImagePath);

		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		Mockito.when(s3Service.getObject(profileImagePath)).thenReturn(imageBytes);

		// First call should fetch from the service and put in the cache
		byte[] result = userService.getUserProfileImage(userId);
		Assertions.assertArrayEquals(imageBytes, result);

		// Cache should contain the image now
		byte[] cachedImage = profileImageCache.getIfPresent(userId);
		Assertions.assertNotNull(cachedImage);
		Assertions.assertArrayEquals(imageBytes, cachedImage);

		// Wait for the cache to expire
		Thread.sleep(11000); // Wait for 11 seconds (slightly longer than the expiration time)

		// Cache should be empty now
		cachedImage = profileImageCache.getIfPresent(userId);
		Assertions.assertNull(cachedImage);
	}*/

}
