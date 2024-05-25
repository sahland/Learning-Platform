package com.knitwit.api.v1.controller;

import com.knitwit.api.v1.request.UserRequest;
import com.knitwit.config.security.KeycloakSecurityUtil;
import com.knitwit.model.Course;
import com.knitwit.model.User;
import com.knitwit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private KeycloakSecurityUtil keycloakUtil;
    @Autowired
    private UserService userService;;

    @Value("${realm}")
    private String realm;

    @Operation(summary = "Зарегистрировать поользователя")
    @PostMapping(value = "/users")
    public ResponseEntity<?> createUserInKeycloakAndDB(@RequestBody UserRequest userRequest) {
        try {
            Response response = createUserInKeycloak(userRequest);
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                String keycloakLogin = userRequest.getLogin(); // Используем login вместо email
                User user = mapUserFromRequest(userRequest);
                user.setKeycloakLogin(keycloakLogin);
                User savedUser = userService.createUser(user);
                return ResponseEntity.ok(savedUser);
            } else if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Пользователь уже существует");
            } else {
                return ResponseEntity.status(response.getStatus()).build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка: " + e.getMessage());
        }
    }

    private Response createUserInKeycloak(UserRequest userRequest) {
        UserRepresentation userRep = mapUserRepFromRequest(userRequest);
        Keycloak keycloak = keycloakUtil.getKeycloakInstance();
        return keycloak.realm(realm).users().create(userRep);
    }

    private UserRepresentation mapUserRepFromRequest(UserRequest userRequest) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(userRequest.getLogin());
        userRep.setEmail(userRequest.getEmail());
        userRep.setEnabled(true);
        userRep.setEmailVerified(true);
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userRequest.getPassword());
        userRep.setCredentials(Arrays.asList(credential));
        return userRep;
    }

    private User mapUserFromRequest(UserRequest userRequest) {
        User user = new User();
        return user;
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

    @Operation(summary = "Добавить аватар пользователя")
    @PostMapping("/{userId}/avatar")
    public ResponseEntity <String> uploadUserAvatar(@PathVariable int userId, @RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadUserAvatar(userId, file);
        return ResponseEntity.ok(avatarUrl);
    }

    @Operation(summary = "Получить аватар пользователя")
    @GetMapping("/{userId}/avatar")
    public ResponseEntity <Resource> getUserAvatar(@PathVariable int userId) {
        Resource avatarResource = userService.getUserAvatar(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(avatarResource);
    }
}