package com.ezpay.usermanagement.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ezpay.usermanagement.entity.User;
import com.ezpay.usermanagement.repository.UserRepository;

@Service
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(User user, String password) {
        validateProfile(user);
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }

        String email = user.getEmail().trim().toLowerCase();
        String mobile = user.getMobileNumber().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            logger.warn("Registration rejected: duplicate email");
            throw new IllegalArgumentException("Email is already registered");
        }
        if (userRepository.existsByMobileNumber(mobile)) {
            logger.warn("Registration rejected: duplicate mobile number");
            throw new IllegalArgumentException("Mobile number is already registered");
        }

        User newUser = new User();
        newUser.setFullName(user.getFullName().trim());
        newUser.setEmail(email);
        newUser.setMobileNumber(mobile);
        newUser.setAddress(user.getAddress());
        newUser.setPasswordHash(passwordEncoder.encode(password));

        User savedUser = userRepository.save(newUser);
        logger.info("User registered successfully. User ID: {}", savedUser.getUserId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User loginUser(String loginId, String password) {
        if (loginId == null || loginId.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Login ID and password are required");
        }

        String identifier = loginId.trim();
        User user = userRepository.findByEmailIgnoreCase(identifier)
            .or(() -> userRepository.findByMobileNumber(identifier))
            .orElseThrow(() -> new IllegalArgumentException("Invalid login credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            logger.warn("Login failed for supplied identifier");
            throw new IllegalArgumentException("Invalid login credentials");
        }

        logger.info("Login successful. User ID: {}", user.getUserId());
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public User updateUserProfile(Long userId, User updatedUser) {
        User existingUser = getUserProfile(userId);

        if (updatedUser == null || updatedUser.getFullName() == null
                || updatedUser.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (updatedUser.getEmail() == null || updatedUser.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (updatedUser.getMobileNumber() == null || updatedUser.getMobileNumber().isBlank()) {
            throw new IllegalArgumentException("Mobile number is required");
        }

        String email = updatedUser.getEmail().trim().toLowerCase();
        String mobile = updatedUser.getMobileNumber().trim();

        userRepository.findByEmailIgnoreCase(email)
            .filter(user -> !user.getUserId().equals(userId))
            .ifPresent(user -> { throw new IllegalArgumentException("Email is already registered"); });

        userRepository.findByMobileNumber(mobile)
            .filter(user -> !user.getUserId().equals(userId))
            .ifPresent(user -> { throw new IllegalArgumentException("Mobile number is already registered"); });

        existingUser.setFullName(updatedUser.getFullName().trim());
        existingUser.setEmail(email);
        existingUser.setMobileNumber(mobile);
        existingUser.setAddress(updatedUser.getAddress());

        User savedUser = userRepository.save(existingUser);
        logger.info("Profile updated. User ID: {}", userId);
        return savedUser;
    }

    @Transactional
    public String requestPasswordReset(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new IllegalArgumentException("Registered email or mobile number is required");
        }

        String identifier = loginId.trim();
        User user = userRepository.findByEmailIgnoreCase(identifier)
            .or(() -> userRepository.findByMobileNumber(identifier))
            .orElseThrow(() -> new IllegalArgumentException("No account found for the supplied identifier"));

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        logger.info("Password reset requested for user ID: {}", user.getUserId());
        // Local demonstration only. Deliver the token through a verified channel in production.
        return resetToken;
    }

    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        if (resetToken == null || resetToken.isBlank()) {
            throw new IllegalArgumentException("Reset token is required");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must contain at least 8 characters");
        }

        User user = userRepository.findByResetToken(resetToken)
            .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        if (user.getResetTokenExpiry() == null
                || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        logger.info("Password reset completed. User ID: {}", user.getUserId());
    }

    private void validateProfile(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User details are required");
        }
        if (user.getFullName() == null || user.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (user.getEmail() == null
                || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("A valid email address is required");
        }
        if (user.getMobileNumber() == null
                || !user.getMobileNumber().matches("[0-9]{10,15}")) {
            throw new IllegalArgumentException("Mobile number must contain 10 to 15 digits");
        }
    }
}
