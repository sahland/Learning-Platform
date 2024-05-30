package com.knitwit.api.v1.dto.mapper;

import com.knitwit.api.v1.dto.request.UserRequest;
import com.knitwit.api.v1.dto.response.UserResponse;
import com.knitwit.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequest userRequest) {
        User user = new User();
        user.setKeycloakLogin(userRequest.getLogin());
        return user;
    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setNickname(user.getNickname());
        response.setAvatarKey(user.getUserAvatarKey());
        response.setLogin(user.getKeycloakLogin());
        return response;
    }
}
