package com.eleven.logistics.auth.presentation.rest.controller.user;

import com.eleven.logistics.auth.application.dto.UserResponseDto;
import com.eleven.logistics.auth.application.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserByName(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
}
