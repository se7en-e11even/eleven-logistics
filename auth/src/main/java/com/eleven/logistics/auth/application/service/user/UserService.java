package com.eleven.logistics.auth.application.service.user;

import com.eleven.logistics.auth.application.dto.UserResponseDto;
import com.eleven.logistics.auth.domain.entity.User;
import com.eleven.logistics.auth.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    // 유저 이름을 통해 유저 조회
    public UserResponseDto getUserByUsername(String username) {

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("등록되지 않은 사용자입니다.")
        );

        return UserResponseDto.of(user);
    }
}