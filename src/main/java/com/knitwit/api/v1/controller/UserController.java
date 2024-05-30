package com.knitwit.api.v1.controller;


import com.knitwit.api.v1.dto.request.UserRequest;
import com.knitwit.api.v1.dto.response.UserResponse;
import com.knitwit.api.v1.dto.mapper.UserMapper;
import com.knitwit.model.Course;
import com.knitwit.model.User;
import com.knitwit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Keycloak")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Зарегистрировать пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно создан", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
    })
    @PostMapping(value = "/registration")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest userRequest) {
        try {
            User savedUser = userService.registerUser(userRequest);
            UserResponse response = userMapper.toResponse(savedUser);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка: " + e.getMessage());
        }
    }

    @Operation(summary = "Получение списка всех пользователей")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        Iterable<User> users = userService.getAllUsers();
        List<UserResponse> response = StreamSupport.stream(users.spliterator(), false)
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение профиля авторизованного пользователя")
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        User user = userService.getUserProfile(username);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение информации о пользователе по его ID")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Смена никнейма пользователя")
    @PutMapping("/nickname")
    public ResponseEntity<UserResponse> updateNickname(@AuthenticationPrincipal Jwt jwt, @RequestParam String newNickname) {
        String username = jwt.getClaim("preferred_username");
        User updated = userService.updateNickname(username, newNickname);
        UserResponse response = userMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Смена email пользователя в Keycloak")
    @PutMapping("/email")
    public ResponseEntity<UserResponse> updateEmailInKeycloak(@AuthenticationPrincipal Jwt jwt, @RequestParam String newEmail) {
        String username = jwt.getClaim("preferred_username");
        User updated = userService.updateEmailInKeycloak(username, newEmail);
        UserResponse response = userMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Смена пароля пользователя в Keycloak")
    @PutMapping("/password")
    public ResponseEntity<UserResponse> updatePasswordInKeycloak(@AuthenticationPrincipal Jwt jwt, @RequestParam String newPassword) {
        String username = jwt.getClaim("preferred_username");
        User updated = userService.updatePasswordInKeycloak(username, newPassword);
        UserResponse response = userMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение всех курсов, на которые подписан пользователь")
    @GetMapping("/courses")
    public ResponseEntity<Set<Course>> getAllSubscribedCourses(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        User user = userService.getUserByKeycloakUsername(username);
        Set<Course> subscribedCourses = userService.getAllSubscribedCourses(user);
        return ResponseEntity.ok(subscribedCourses);
    }

    @Operation(summary = "Добавить аватар пользователя")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadUserAvatar(@AuthenticationPrincipal Jwt jwt, @RequestParam("file") MultipartFile file) {
        String username = jwt.getClaim("preferred_username");
        String avatarUrl = userService.uploadUserAvatar(username, file);
        return ResponseEntity.ok(avatarUrl);
    }
}
