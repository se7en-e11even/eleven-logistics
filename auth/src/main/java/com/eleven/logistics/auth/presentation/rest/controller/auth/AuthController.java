package com.eleven.logistics.auth.presentation.rest.controller.auth;

import com.eleven.logistics.auth.application.dto.UserResponseDto;
import com.eleven.logistics.auth.application.service.auth.AuthService;
import com.eleven.logistics.auth.presentation.rest.dto.SignInRequestDto;
import com.eleven.logistics.auth.presentation.rest.dto.SignUpRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
        return ResponseEntity.ok(authService.signUp(signUpRequestDto.toCommand()));
    }

    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(@Valid @RequestBody SignInRequestDto signInRequestDto) {
        String accessToken = authService.signIn(signInRequestDto.toCommand());
        // 응답 헤더에 accessToken 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        return ResponseEntity.ok()
                .headers(headers)
                .body("로그인이 성공했습니다.");
    }
}