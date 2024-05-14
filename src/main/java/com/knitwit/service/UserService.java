package com.knitwit.service;

import com.knitwit.model.Course;
import com.knitwit.model.MediaFile;
import com.knitwit.model.User;
import com.knitwit.repository.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Schema(description = "Сервис для работы с пользователем")
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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

    @Transactional
    public void addAvatarToUser(int userId, MediaFile avatarFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с указанным ID не найден: " + userId));
        user.setUserAvatar(avatarFile);
        userRepository.save(user);
    }

    @Transactional
    public void removeAvatarFromUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с указанным ID не найден: " + userId));
        user.setUserAvatar(null);
        userRepository.save(user);
    }

    public Optional<User> findById(int userId) {
        return userRepository.findById(userId);
    }

}