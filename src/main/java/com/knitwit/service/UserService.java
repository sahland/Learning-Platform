package com.knitwit.service;

import com.knitwit.api.v1.dto.request.UserRequest;
import com.knitwit.config.security.KeycloakSecurityUtil;
import com.knitwit.model.Course;
import com.knitwit.model.User;
import com.knitwit.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MinioService minioService;
    private final KeycloakSecurityUtil keycloakUtil;

    @Value("${realm}")
    private String realm;

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByKeycloakLogin(user.getKeycloakLogin())) {
            throw new IllegalArgumentException("Пользователь с этим логином уже существует.");
        }

        User savedUser = userRepository.save(user);
        savedUser.setNickname("user" + savedUser.getUserId());
        return userRepository.save(savedUser);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден по id: " + userId));
    }

    @Transactional
    public User getUserProfile(String username) {
        return userRepository.findByKeycloakLogin(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Transactional
    public User updateNickname(String username, String newNickname) {
        User user = userRepository.findByKeycloakLogin(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        user.setNickname(newNickname);
        return userRepository.save(user);
    }

    @Transactional
    public User updateEmailInKeycloak(String username, String newEmail) {
        keycloakUtil.updateUserEmail(username, newEmail);
        return userRepository.findByKeycloakLogin(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Transactional
    public User updatePasswordInKeycloak(String username, String newPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        keycloakUtil.updateUserPassword(username, credential);
        return userRepository.findByKeycloakLogin(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    public Set<Course> getAllSubscribedCourses(User user) {
        return user.getCourses();
    }

    public Optional<User> findById(int userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public String uploadUserAvatar(String keycloakLogin, MultipartFile file) {
        try {
            User user = userRepository.findByKeycloakLogin(keycloakLogin)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
            int userId = user.getUserId();
            String objectName = "user_avatars/user_" + userId + "_avatar.jpg";
            String previousAvatarKey = user.getUserAvatarKey();
            if (previousAvatarKey != null) {
                minioService.deleteFile(previousAvatarKey);
            }
            InputStream inputStream = file.getInputStream();
            minioService.uploadFile(objectName, inputStream);
            user.setUserAvatarKey(objectName);
            userRepository.save(user);
            return objectName;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить аватар пользователя", e);
        }
    }

    public User getUserByKeycloakUsername(String username) {
        UserRepresentation keycloakUser = keycloakUtil.getUserByUsername(username);
        return userRepository.findByKeycloakLogin(keycloakUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Transactional
    public User registerUser(UserRequest userRequest) {
        Response response = createUserInKeycloak(userRequest);
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            String keycloakLogin = userRequest.getLogin();
            User user = mapUserFromRequest(userRequest);
            user.setKeycloakLogin(keycloakLogin);
            return createUser(user);
        } else if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
            throw new IllegalArgumentException("Пользователь уже существует");
        } else {
            throw new RuntimeException("Ошибка при создании пользователя в Keycloak: " + response.getStatus());
        }
    }

    @Transactional
    public Response createUserInKeycloak(UserRequest userRequest) {
        UserRepresentation userRep = mapUserRepFromRequest(userRequest);
        Keycloak keycloak = keycloakUtil.getKeycloakInstance();

        // Проверка на корректность инициализации keycloak и realm
        if (keycloak == null) {
            throw new RuntimeException("Keycloak instance is not initialized.");
        }

        if (realm == null || realm.isEmpty()) {
            throw new RuntimeException("Realm is not configured correctly.");
        }

        return keycloak.realm(realm).users().create(userRep);
    }

    public UserRepresentation mapUserRepFromRequest(UserRequest userRequest) {
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

    public User mapUserFromRequest(UserRequest userRequest) {
        User user = new User();
        return user;
    }
}
