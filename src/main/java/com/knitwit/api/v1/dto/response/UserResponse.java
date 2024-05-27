package com.knitwit.api.v1.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private int userId;
    private String nickname;
    private String avatarKey;
    private String login;
}
