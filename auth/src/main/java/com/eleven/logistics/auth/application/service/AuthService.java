package com.eleven.logistics.auth.application.service;

import com.eleven.logistics.auth.application.dto.UserResponseDto;
import com.eleven.logistics.auth.domain.entity.User;
import com.eleven.logistics.auth.domain.repository.UserRepository;
import com.eleven.logistics.auth.presentation.rest.dto.SignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;



    @Transactional
    public UserResponseDto signUp(SignUpRequestDto signUpRequestDto) {

        // 중복 체크
        if (userRepository.findByUsername(signUpRequestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

        // User 엔티티에 객체 생성에 대한 책임을 부여합니다.
        User user = User.create(
                signUpRequestDto.getUsername(),
                hashedPassword,
                signUpRequestDto.getSlackAccount(),
                signUpRequestDto.getRole()
        );

        userRepository.save(user);

        // Dirty Checking 으로 User 엔티티의 Id가 추가되어 반환됩니다.
        return UserResponseDto.of(user);
    }
}