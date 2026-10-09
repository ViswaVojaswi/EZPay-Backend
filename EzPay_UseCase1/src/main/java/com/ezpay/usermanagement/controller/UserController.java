package com.ezpay.usermanagement.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ezpay.usermanagement.entity.User;
import com.ezpay.usermanagement.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger =
            LogManager.getLogger(UserController.class);

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(
            @RequestBody Map<String, Object> request) {

        String password = (String) request.remove("password");

        User user = objectMapper.convertValue(request, User.class);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerUser(user, password));
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(
            @RequestBody Map<String, String> request) {

        String loginId = request.get("loginId");
        String password = request.get("password");

        return ResponseEntity.ok(
                userService.loginUser(loginId, password));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserProfile(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody User updatedUser) {
        return ResponseEntity.ok(
                userService.updateUserProfile(userId, updatedUser));
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(
            @RequestBody Map<String, String> request) {

        String resetToken =
                userService.requestPasswordReset(request.get("loginId"));

        logger.warn("Password reset token generated for local demonstration");

        return ResponseEntity.ok(Map.of(
                "message", "Reset token generated for local testing",
                "resetToken", resetToken));
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody Map<String, String> request) {

        userService.resetPassword(
                request.get("resetToken"),
                request.get("newPassword"));

        return ResponseEntity.ok(
                Map.of("message", "Password reset successfully"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidInput(
            IllegalArgumentException exception) {

        return ResponseEntity.badRequest().body(
                Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpectedError(
            Exception exception) {

        logger.error("Unexpected error in UserController", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error",
                        "An unexpected server error occurred"));
    }
}