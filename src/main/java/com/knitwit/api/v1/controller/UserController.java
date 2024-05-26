package com.knitwit.api.v1.controller;

import com.knitwit.api.v1.request.UserRequest;
import com.knitwit.model.Course;
import com.knitwit.model.User;
import com.knitwit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Keycloak")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Зарегистрировать пользователя")
    @PostMapping(value = "/users")
    //public
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest userRequest) {
        try {
            User savedUser = userService.registerUser(userRequest);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка: " + e.getMessage());
        }
    }

    @Operation(summary = "Получение списка всех пользователей")
    @GetMapping
    //admin
    public ResponseEntity<Iterable<User>> getAllUsers() {
        Iterable<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Получение профиля авторизованного пользователя")
    @GetMapping("/profile")
    //user
    public ResponseEntity<User> getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        User user = userService.getUserProfile(username);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Получение информации о пользователе по его ID")
    @GetMapping("/{userId}")
    //admin
    public ResponseEntity<User> getUserById(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Смена никнейма пользователя")
    @PutMapping("/nickname")
    //user
    public ResponseEntity<User> updateNickname(@AuthenticationPrincipal Jwt jwt, @RequestParam String newNickname) {
        String username = jwt.getClaim("preferred_username");
        User updated = userService.updateNickname(username, newNickname);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Смена email пользователя в Keycloak")
    @PutMapping("/email")
    //user
    public ResponseEntity<User> updateEmailInKeycloak(@AuthenticationPrincipal Jwt jwt, @RequestParam String newEmail) {
        String username = jwt.getClaim("preferred_username");
        User updated = userService.updateEmailInKeycloak(username, newEmail);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Смена пароля пользователя в Keycloak")
    @PutMapping("/password")
    //user
    public ResponseEntity<User> updatePasswordInKeycloak(@AuthenticationPrincipal Jwt jwt, @RequestParam String newPassword) {
        String username = jwt.getClaim("preferred_username");
        User updated = userService.updatePasswordInKeycloak(username, newPassword);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Получение всех курсов, на которые подписан пользователь")
    @GetMapping("/courses")
    //user
    public ResponseEntity<Set<Course>> getAllSubscribedCourses(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        User user = userService.getUserByKeycloakUsername(username);
        Set<Course> subscribedCourses = userService.getAllSubscribedCourses(user);
        return ResponseEntity.ok(subscribedCourses);
    }

    @Operation(summary = "Добавить аватар пользователя")
    @PostMapping("/avatar")
    //user
    public ResponseEntity<String> uploadUserAvatar(@AuthenticationPrincipal Jwt jwt, @RequestParam("file") MultipartFile file) {
        String username = jwt.getClaim("preferred_username");
        String avatarUrl = userService.uploadUserAvatar(username, file);
        return ResponseEntity.ok(avatarUrl);
    }
}