package com.knitwit.api.v1.controller;

import com.knitwit.api.v1.dto.request.JwtRequest;
import com.knitwit.api.v1.dto.request.RegistrationUserRequest;
import com.knitwit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Авторизирвоаться")
    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        return authService.createAuthToken(authRequest);
    }

    @Operation(summary = "Зарегистрироваться")
    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserRequest registrationUserRequest) {
        return authService.createNewrUser(registrationUserRequest);
    }
}
