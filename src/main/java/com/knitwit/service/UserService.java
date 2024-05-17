package com.knitwit.service;

import com.knitwit.model.Course;
import com.knitwit.model.User;
import com.knitwit.repository.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MinioService minioService;

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    }
    @Transactional
    public User updateUser(int userId, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            updatedUser.setUserId(userId);
            return userRepository.save(updatedUser);
        } else {
            throw new IllegalArgumentException("User not found with id: " + userId);
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
            throw new RuntimeException("Failed to upload user avatar", e);
        }
    }

    @Transactional
    public Resource getUserAvatar(int userId) {
        User user = getUserById(userId);
        if (user == null || user.getUserAvatarKey() == null) {
            throw new RuntimeException("User avatar key is null for user with ID: " + userId);
        }
        String objectName = user.getUserAvatarKey();
        return minioService.getFileResource(objectName);
    }
}