package com.knitwit.service;

import com.knitwit.api.v1.dto.request.RegistrationUserRequest;
import com.knitwit.model.Course;
import com.knitwit.model.Role;
import com.knitwit.model.User;
import com.knitwit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MinioService minioService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;

    @Transactional
    public User createNewUser(RegistrationUserRequest registrationUserRequest) {
        User user = new User();
        user.setEmail(registrationUserRequest.getEmail());
        user.setUsername(registrationUserRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationUserRequest.getPassword()));
        String nickname = "user";
        user.setNickname(nickname);
        user.setRoles(List.of(roleService.getUserRole()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User updateEmail(String username, String newEmail) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        user.setEmail(newEmail);
        return userRepository.save(user);
    }

    @Transactional
    public User updatePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
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
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Transactional
    public User updateNickname(String username, String newNickname) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        user.setNickname(newNickname);
        return userRepository.save(user);
    }

    public Set<Course> getAllSubscribedCourses(User user) {
        return user.getCourses();
    }

    public Optional<User> findById(int userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public String uploadUserAvatar(String username, MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new IllegalArgumentException("Недопустимый тип файла. Разрешены только файлы JPEG и PNG.");
            }
            User user = userRepository.findByUsername(username)
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

    @Transactional
    public User addAdminRoleToUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        Role adminRole = roleService.getAdminRole();
        user.getRoles().add(adminRole);
        return userRepository.save(user);
    }
}
