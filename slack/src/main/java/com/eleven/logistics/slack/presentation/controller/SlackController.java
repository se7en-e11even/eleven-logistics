package com.eleven.logistics.slack.presentation.controller;

import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.slack.application.dto.PageResponseDto;
import com.eleven.logistics.slack.application.service.SlackService;
import com.eleven.logistics.slack.application.slackdto.SlackMessageResponse;
import com.eleven.logistics.slack.presentation.dto.UpdateSlackMessageRequestDto;
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
    public ResponseEntity<ApiResponseDto<SlackMessageResponse>> createSlackMessage(
            @RequestParam("username") String slackUsername,
            @RequestHeader("X-Role") String role) {
        if (role.isEmpty()) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        SlackMessageResponse response = slackService.sendMessageSlackController(slackUsername);
        return ResponseEntity.ok(ApiResponseDto.success(response, "요청이 성공적으로 전달 되었습니다."));
    }

    @GetMapping("/{slackId}")
    public ResponseEntity<ApiResponseDto<SlackMessageResponse>> findBySlackId(
            @PathVariable UUID slackId,
            @RequestHeader("X-Role") String role) {
        if (role == null || !role.equals("MASTER")) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        SlackMessageResponse response = slackService.findBySlackId(slackId);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(response, "요청이 성공적으로 처리되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<PageResponseDto<SlackMessageResponse>>> findByAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-Role") String role) {
        if (role == null || !role.equals("MASTER")) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        PageResponseDto<SlackMessageResponse> response = slackService.findByAll(page - 1, size);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(response, "요청이 성공적으로 처리되었습니다."));
    }

    @PutMapping("/{slackId}")
    public ResponseEntity<ApiResponseDto<SlackMessageResponse>> updateSlackMessage(
            @RequestBody UpdateSlackMessageRequestDto requestDto,
            @PathVariable UUID slackId,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role) {
        if (role == null || !role.equals("MASTER")) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        SlackMessageResponse response = slackService.updateSlackMessage(slackId, requestDto.toDto(), username);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(response, "요청이 성공적으로 처리되었습니다."));
    }

    @DeleteMapping("/{slackId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteSlackMessage(
            @PathVariable UUID slackId,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role) {
        if (role == null || !role.equals("MASTER")) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        slackService.deleteSlackMessage(slackId, username);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(null, "요청이 성공적으로 처리되었습니다."));
    }
}
