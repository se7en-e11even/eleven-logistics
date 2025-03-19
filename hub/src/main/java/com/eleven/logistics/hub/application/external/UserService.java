package com.eleven.logistics.hub.application.external;

import com.eleven.logistics.hub.application.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserService {

    ResponseEntity<UserResponseDto> getUserByName(@PathVariable String username);
}
