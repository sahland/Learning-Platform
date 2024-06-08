package com.knitwit.api.v1.dto.mapper;

import com.knitwit.api.v1.dto.request.UserRequest;
import com.knitwit.api.v1.dto.response.UserResponse;
import com.knitwit.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        return user;
    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setNickname(user.getNickname());
        response.setAvatarKey(user.getUserAvatarKey());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }
}
