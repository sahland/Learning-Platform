package com.knitwit.api.v1.controller;


import com.knitwit.api.v1.dto.mapper.CourseMapper;
import com.knitwit.api.v1.dto.response.CourseResponse;
import com.knitwit.api.v1.dto.response.UserResponse;
import com.knitwit.api.v1.dto.mapper.UserMapper;
import com.knitwit.model.Course;
import com.knitwit.model.User;
import com.knitwit.service.AuthService;
import com.knitwit.service.CourseService;
import com.knitwit.service.LearningProgressService;
import com.knitwit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final CourseMapper courseMapper;
    private final LearningProgressService learningProgressService;

    @Operation(summary = "Получение списка всех пользователей (ADMIN)")
    @Secured("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        Iterable<User> users = userService.getAllUsers();
        List<UserResponse> response = StreamSupport.stream(users.spliterator(), false)
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение профиля авторизованного пользователя (USER)")
    @Secured("ROLE_USER")
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile() {
        String username = authService.getCurrentUsername();
        User user = userService.getUserProfile(username);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение информации о пользователе по его ID (ADMIN)")
    @Secured("ROLE_ADMIN")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Смена никнейма пользователя (USER)")
    @Secured("ROLE_USER")
    @PutMapping("/nickname")
    public ResponseEntity<UserResponse> updateNickname(@RequestParam String newNickname) {
        String username = authService.getCurrentUsername();
        User updated = userService.updateNickname(username, newNickname);
        UserResponse response = userMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Смена электронной почты пользователя (USER)")
    @Secured("ROLE_USER")
    @PutMapping("/email")
    public ResponseEntity<UserResponse> updateEmail(@RequestParam String newEmail) {
        String username = authService.getCurrentUsername();
        User updated = userService.updateEmail(username, newEmail);
        UserResponse response = userMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Смена пароля пользователя (USER)")
    @Secured("ROLE_USER")
    @PutMapping("/password")
    public ResponseEntity<UserResponse> updatePassword(@RequestParam String newPassword) {
        String username = authService.getCurrentUsername();
        User updated = userService.updatePassword(username, newPassword);
        UserResponse response = userMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Добавить аватар пользователя (USER)")
    @Secured("ROLE_USER")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadUserAvatar(@RequestParam("file") MultipartFile file) {
        String username = authService.getCurrentUsername();
        String avatarUrl = userService.uploadUserAvatar(username, file);
        return ResponseEntity.ok(avatarUrl);
    }

    @Operation(summary = "Добавить роль администратора пользователю (ADMIN)")
    @Secured("ROLE_ADMIN")
    @PostMapping("/{username}/addAdminRole")
    public ResponseEntity<UserResponse> addAdminRoleToUser(@RequestParam String username) {
        userService.addAdminRoleToUser(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получение курсов, на которые подписан пользователь (USER)")
    @Secured("ROLE_USER")
    @GetMapping("/subscribed-courses")
    public ResponseEntity<List<CourseResponse>> getSubscribedCourses() {
        String username = authService.getCurrentUsername();
        User user = userService.getUserProfile(username);
        Set<Course> subscribedCourses = userService.getAllSubscribedCourses(user);
        List<CourseResponse> response = subscribedCourses.stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить список курсов, в которых пользователь завершил все разделы (USER)")
    @Secured("ROLE_USER")
    @GetMapping("/completed-courses")
    public ResponseEntity<List<CourseResponse>> getCompletedCoursesForUser() {
        String username = authService.getCurrentUsername();
        User user = userService.getUserProfile(username);
        List<Course> completedCourses = learningProgressService.getCompletedCoursesForUser(user.getUserId());
        List<CourseResponse> response = completedCourses.stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
