package com.knitwit.api.v1.dto.request;

import lombok.Data;

@Data
public class RegistrationUserRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
}
