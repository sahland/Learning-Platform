package com.knitwit.service;

import com.knitwit.model.Course;
import com.knitwit.model.User;
import com.knitwit.repository.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

@Schema(description = "Сервис для работы с пользователем")
@Service
public class UserService {

    private final UserRepository userRepository;
    private final MinioService minioService;

    public UserService(UserRepository userRepository, MinioService minioService) {
        this.userRepository = userRepository;
        this.minioService = minioService;
    }

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден по id: " + userId));
    }
    @Transactional
    public User updateUser(int userId, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            updatedUser.setUserId(userId);
            return userRepository.save(updatedUser);
        } else {
            throw new IllegalArgumentException("Пользователь не найден по ID: " + userId);
        }
    }

    public Set<Course> getAllSubscribedCourses(User user) {
        return user.getCourses();
    }

    public Optional<User> findById(int userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public String uploadUserAvatar(int userId, MultipartFile file) {
        try {
            String objectName = "user_avatars/user_" + userId + "_avatar.jpg";
            User user = getUserById(userId);
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

    public Resource getUserAvatar(int userId) {
        User user = getUserById(userId);
        if (user == null || user.getUserAvatarKey() == null) {
            throw new RuntimeException("Ключ аватара пользователя имеет значение null для пользователя с ID: " + userId);
        }
        String objectName = user.getUserAvatarKey();
        return minioService.getFileResource(objectName);
    }
}