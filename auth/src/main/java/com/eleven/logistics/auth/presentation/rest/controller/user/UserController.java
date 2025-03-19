package com.eleven.logistics.auth.presentation.rest.controller.user;

import com.eleven.logistics.auth.application.dto.UserResponseDto;
import com.eleven.logistics.auth.application.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserByName(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
}
