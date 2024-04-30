package com.knitwit.service;

import com.knitwit.model.Course;
import com.knitwit.model.User;
import com.knitwit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Создание пользователя
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Удаление пользователя
    @Transactional
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    // Редактирование пользователя
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

    // Получение списка всех пользователей
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Получение пользователя по его идентификатору
    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    }

    //получение всех курсов, на которые подписан пользователь
    public Set<Course> getAllSubscribedCourses(User user) {
        return user.getCourses();
    }
}