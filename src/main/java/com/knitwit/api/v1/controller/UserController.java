package com.knitwit.api.v1.controller;


import com.knitwit.api.v1.dto.response.UserResponse;
import com.knitwit.api.v1.dto.mapper.UserMapper;
import com.knitwit.model.User;
import com.knitwit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

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

    @Operation(summary = "Смена электронной почты пользователя")
    @PutMapping("/email")
    public ResponseEntity<UserResponse> updateEmail(@AuthenticationPrincipal Jwt jwt, @RequestParam String newEmail) {
        String username = jwt.getClaim("preferred_username");
        User updated = userService.updateEmail(username, newEmail);
        UserResponse response = userMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Смена пароля пользователя")
    @PutMapping("/password")
    public ResponseEntity<UserResponse> updatePassword(@AuthenticationPrincipal Jwt jwt, @RequestParam String newPassword) {
        String username = jwt.getClaim("preferred_username");
        User updated = userService.updatePassword(username, newPassword);
        UserResponse response = userMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Добавить аватар пользователя")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadUserAvatar(@AuthenticationPrincipal Jwt jwt, @RequestParam("file") MultipartFile file) {
        String username = jwt.getClaim("preferred_username");
        String avatarUrl = userService.uploadUserAvatar(username, file);
        return ResponseEntity.ok(avatarUrl);
    }
}
