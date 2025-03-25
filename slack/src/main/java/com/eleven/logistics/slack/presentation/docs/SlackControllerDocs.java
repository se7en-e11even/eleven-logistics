package com.eleven.logistics.slack.presentation.docs;

import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.slack.application.slackdto.SlackMessageResponse;
import com.eleven.logistics.slack.presentation.dto.UpdateSlackMessageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "슬랙", description = "슬랙 메시지 생성, 검색, 수정, 삭제 등의 API")
public interface SlackControllerDocs {

    @PostMapping("/api/slack")
    @Operation(summary = "슬랙 메시지 생성 및 전송", description = "배송 담당자에게 전달할 배송정보를 생성하고 전달하는 API 입니다.")
    ResponseEntity<ApiResponseDto<SlackMessageResponse>> createSlackMessage(
            @RequestParam("username") String slackUsername,
            @RequestHeader("X-Role") String role);

    @GetMapping("/api/slack/{slackId}")
    @Operation(summary = "슬랙 메시지 단건 조회", description = "슬랙 메시지를 단건 조회하는 API 입니다.")
    ResponseEntity<ApiResponseDto<SlackMessageResponse>> findBySlackId(
            @PathVariable UUID slackId,
            @RequestHeader("X-Role") String role);

    @GetMapping("/api/slack")
    @Operation(summary = "슬랙 메시지 조회 검색", description = "슬랙 메시지를 전체 조회하는 API 입니다.")
    ResponseEntity<ApiResponseDto<com.eleven.logistics.slack.application.dto.PageResponseDto<SlackMessageResponse>>> findByAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-Role") String role);

    @PutMapping("/api/slack/{slackId}")
    @Operation(summary = "슬랙 메시지 수정 및 전송", description = "배송 담당자에게 전달할 배송정보를 수정하고 전달하는 API 입니다.")
    ResponseEntity<ApiResponseDto<SlackMessageResponse>> updateSlackMessage(
            @RequestBody UpdateSlackMessageRequestDto requestDto,
            @PathVariable UUID slackId,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role);

    @DeleteMapping("/api/slack/{slackId}")
    @Operation(summary = "슬랙 메시지 삭제", description = "슬랙 메시지를 삭제하는 API 입니다.")
    ResponseEntity<ApiResponseDto<Void>> deleteSlackMessage(
            @PathVariable UUID slackId,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role);
}