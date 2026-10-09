package com.ezpay.usermanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ezpay.usermanagement.entity.User;
import com.ezpay.usermanagement.repository.UserRepository;
import com.ezpay.usermanagement.service.UserService;

@SpringBootTest
class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private String email;
    private String mobile;

    @BeforeEach
    void setUp() {
        String uniqueValue = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        email = "test" + uniqueValue + "@example.com";
        mobile = "9" + String.format("%09d",
                Math.abs(System.nanoTime()) % 1_000_000_000L);
    }

    @AfterEach
    void cleanUp() {
        userRepository.findByEmailIgnoreCase(email).ifPresent(userRepository::delete);
    }

    private User createUser() {
        User user = new User();
        user.setFullName("Test User");
        user.setEmail(email);
        user.setMobileNumber(mobile);
        user.setAddress("Test Address");
        return user;
    }

    @Test
    void registerUserSuccessfully() {
        User savedUser = userService.registerUser(createUser(), "TestPass123!");
        assertNotNull(savedUser.getUserId());
        assertEquals(email, savedUser.getEmail());
        assertNotNull(savedUser.getPasswordHash());
        assertTrue(!savedUser.getPasswordHash().equals("TestPass123!"));
    }

    @Test
    void rejectDuplicateEmail() {
        userService.registerUser(createUser(), "TestPass123!");
        User duplicateUser = createUser();
        duplicateUser.setMobileNumber("8" + mobile.substring(1));
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(duplicateUser, "AnotherPass123!"));
    }

    @Test
    void loginWithEmailSuccessfully() {
        userService.registerUser(createUser(), "TestPass123!");
        User loggedInUser = userService.loginUser(email, "TestPass123!");
        assertNotNull(loggedInUser.getUserId());
        assertEquals(email, loggedInUser.getEmail());
    }

    @Test
    void rejectIncorrectPassword() {
        userService.registerUser(createUser(), "TestPass123!");
        assertThrows(IllegalArgumentException.class,
                () -> userService.loginUser(email, "WrongPass123!"));
    }

    @Test
    void updateProfileSuccessfully() {
        User savedUser = userService.registerUser(createUser(), "TestPass123!");
        User updatedUser = new User();
        updatedUser.setFullName("Updated User");
        updatedUser.setEmail(email);
        updatedUser.setMobileNumber(mobile);
        updatedUser.setAddress("Updated Address");

        User result = userService.updateUserProfile(savedUser.getUserId(), updatedUser);
        assertEquals("Updated User", result.getFullName());
        assertEquals("Updated Address", result.getAddress());
    }

    @Test
    void resetPasswordSuccessfully() {
        userService.registerUser(createUser(), "TestPass123!");
        String resetToken = userService.requestPasswordReset(email);
        userService.resetPassword(resetToken, "NewPass123!");

        assertNotNull(userService.loginUser(email, "NewPass123!").getUserId());
        assertThrows(IllegalArgumentException.class,
                () -> userService.loginUser(email, "TestPass123!"));
    }
}
