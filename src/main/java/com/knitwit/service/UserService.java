package com.knitwit.service;

import com.knitwit.entity.User;
import com.knitwit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User addUser(User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("New user should not have an id");
        }
        return userRepository.save(user);
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    updatedUser.setId(id);
                    return userRepository.save(updatedUser);
                });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
