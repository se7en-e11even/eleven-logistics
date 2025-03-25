package com.eleven.logistics.hub.presentation.docs;

import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.hub.application.dto.PageResponseDto;
import com.eleven.logistics.hub.application.dto.hub.HubResponseDto;
import com.eleven.logistics.hub.presentation.dto.hub.HubRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "허브", description = "허브 생성, 검색, 수정, 삭제 등의 API")
public interface HubControllerDocs {

    @PostMapping("/api/hub")
    @Operation(summary = "허브 생성", description = "허브를 생성하는 API 입니다.")
    ResponseEntity<ApiResponseDto<HubResponseDto>> createHub(@RequestBody HubRequestDto requestDto,
                                                             @RequestHeader("X-Username") String username,
                                                             @RequestHeader("X-Role") String role);

    @GetMapping("/api/hub/{hubId}/company")
    @Operation(summary = "허브 단건 조회", description = "허브를 단건 조회할 때 소속되어 있는 업체도 조회 가능한 API 입니다.")
    ResponseEntity<ApiResponseDto<HubResponseDto>> findByHubIdAndCompany(@PathVariable("hubId") UUID hubId,
                                                                         @RequestParam(defaultValue = "1") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestHeader("X-Role") String role);

    @GetMapping("/api/hub")
    @Operation(summary = "허브 전체 검색", description = "전체 허브를 검색하는 API 입니다.")
    ResponseEntity<ApiResponseDto<PageResponseDto<HubResponseDto>>> findByAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size);

    @PutMapping("/api/hub/{hubId}")
    @Operation(summary = "허브 수정", description = "허브를 수정하는 API 입니다.")
    ResponseEntity<ApiResponseDto<HubResponseDto>> updateHub(@PathVariable("hubId") UUID hubId,
                                                             @RequestBody HubRequestDto requestDto,
                                                             @RequestHeader("X-Username") String username,
                                                             @RequestHeader("X-Role") String role);

    @DeleteMapping("/api/hub/{hubId}")
    @Operation(summary = "허브 삭제", description = "허브를 삭제하는 API 입니다.")
    ResponseEntity<ApiResponseDto<Void>> deleteHub(@PathVariable("hubId") UUID hubId,
                                                   @RequestHeader("X-Username") String username,
                                                   @RequestHeader("X-Role") String role);
}