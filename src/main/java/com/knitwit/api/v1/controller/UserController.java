package com.knitwit.api.v1.controller;

import com.knitwit.model.Course;
import com.knitwit.model.User;
import com.knitwit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Создание пользователя")
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @Operation(summary = "Получение списка всех пользователей")
    @GetMapping
    public ResponseEntity<Iterable<User>> getAllUsers() {
        Iterable<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Получение пользователя по его ID")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Редактирование пользователя")
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable int userId, @RequestBody User updatedUser) {
        User updated = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Получение всех курсов, на которые подписан пользователь")
    @GetMapping("/{userId}/courses")
    public ResponseEntity<Set<Course>> getAllSubscribedCourses(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        Set<Course> subscribedCourses = userService.getAllSubscribedCourses(user);
        return ResponseEntity.ok(subscribedCourses);
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity <String> uploadUserAvatar(@PathVariable int userId, @RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadUserAvatar(userId, file);
        return ResponseEntity.ok(avatarUrl);
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity <Resource> getUserAvatar(@PathVariable int userId) {
        Resource avatarResource = userService.getUserAvatar(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(avatarResource);
    }
}