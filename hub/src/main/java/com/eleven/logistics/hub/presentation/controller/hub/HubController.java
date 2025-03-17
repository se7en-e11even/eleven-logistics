package com.eleven.logistics.hub.presentation.controller.hub;


import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.hub.application.dto.PageResponseDto;
import com.eleven.logistics.hub.application.dto.hub.HubResponseDto;
import com.eleven.logistics.hub.application.service.hub.HubService;
import com.eleven.logistics.hub.presentation.dto.hub.HubRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class HubController {

    private final HubService hubService;

    @PostMapping("/hub")
    public ResponseEntity<ApiResponseDto<HubResponseDto>> createHub(@RequestBody HubRequestDto requestDto){
        HubResponseDto responseDto = hubService.createHub(requestDto.toDto());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @GetMapping("/hub/{hubId}")
    public ResponseEntity<ApiResponseDto<HubResponseDto>> findByHubId(@PathVariable("hubId") UUID hubId,
                                                                      @RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10")int size){
        HubResponseDto responseDto = hubService.findByHubId(hubId, page-1, size);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @GetMapping("/hub")
    public ResponseEntity<ApiResponseDto<PageResponseDto<HubResponseDto>>> findByAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10")int size){
        PageResponseDto<HubResponseDto> responseDto = hubService.findByAll(page-1, size);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @PutMapping("/hub/{hubId}")
    public ResponseEntity<ApiResponseDto<HubResponseDto>> updateHub(@PathVariable("hubId") UUID hubId,
                                                                    @RequestBody HubRequestDto requestDto){
        HubResponseDto responseDto = hubService.updateHub(hubId, requestDto.toDto());
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @DeleteMapping("/hub/{hubId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteHub(@PathVariable("hubId") UUID hubId){
        hubService.deleteHub(hubId);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(null, "요청이 성공적으로 처리되었습니다."));
    }

}
