package com.eleven.logistics.auth.presentation.rest.controller.auth;

import com.eleven.logistics.auth.application.dto.UserResponseDto;
import com.eleven.logistics.auth.application.service.AuthService;
import com.eleven.logistics.auth.presentation.rest.dto.SignUpRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;


    @PostMapping("/signUp")
    public ResponseEntity<UserResponseDto> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        return ResponseEntity.ok(authService.signUp(signUpRequestDto));
    }
}