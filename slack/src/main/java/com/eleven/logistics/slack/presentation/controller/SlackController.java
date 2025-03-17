package com.eleven.logistics.slack.presentation.controller;

import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.slack.application.dto.CreateSlackMessageDto;

import com.eleven.logistics.slack.application.dto.SlackMessageResponse;
import com.eleven.logistics.slack.application.service.SlackService;
import com.eleven.logistics.slack.presentation.dto.CreateSlackMessageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/slack")
public class SlackController {

    private final SlackService slackService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<SlackMessageResponse>> createSlackMessage(@RequestBody CreateSlackMessageRequestDto requestDto,
                                                                                   @RequestHeader("X-Username") String username,
                                                                                   @RequestHeader("X-Role") String role) {
        if(role.isEmpty()) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        SlackMessageResponse response = slackService.sendMessageToAllUsers(requestDto.toDto(), username);
        return ResponseEntity.ok(ApiResponseDto.success(response, "요청이 성공적으로 전달 되었습니다."));
    }

    @GetMapping("/{slackId}")
    public ResponseEntity<ApiResponseDto<?>> findBySlackId(@PathVariable UUID slackId,
                                                           @RequestHeader("X-Username") String username,
                                                           @RequestHeader("X-Role") String role){
        if(role == null || !role.equals("MASTER")) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<?>> findByAll(@RequestHeader("X-Username") String username,
                                                       @RequestHeader("X-Role") String role) {
        if(role == null || !role.equals("MASTER")) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        return null;
    }

    @PutMapping("/{slackId}")
    public ResponseEntity<ApiResponseDto<?>> updateSlackMessage(@PathVariable UUID slackId,
                                                                @RequestHeader("X-Username") String username,
                                                                @RequestHeader("X-Role") String role){
        if(role == null || !role.equals("MASTER")) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        return null;
    }

    @DeleteMapping("/{slackId}")
    public ResponseEntity<ApiResponseDto<?>> deleteSlackMessage(@PathVariable UUID slackId,
                                                                @RequestHeader("X-Username") String username,
                                                                @RequestHeader("X-Role") String role) {
        if(role == null || !role.equals("MASTER")) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        return null;
    }
}
