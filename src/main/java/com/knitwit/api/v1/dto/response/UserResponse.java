package com.knitwit.api.v1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private int userId;
    private String username;
    private String avatarKey;
    private String email;
    private String nickname;
}
