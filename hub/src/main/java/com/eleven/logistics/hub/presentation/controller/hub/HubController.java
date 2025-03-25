package com.eleven.logistics.hub.presentation.controller.hub;


import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.hub.application.dto.PageResponseDto;
import com.eleven.logistics.hub.application.dto.hub.HubResponseDto;
import com.eleven.logistics.hub.application.service.hub.HubService;
import com.eleven.logistics.hub.presentation.docs.HubControllerDocs;
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
@RequestMapping("/api/hub")
public class HubController implements HubControllerDocs {

    private final HubService hubService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<HubResponseDto>> createHub(@RequestBody HubRequestDto requestDto,
                                                                    @RequestHeader("X-Username") String username,
                                                                    @RequestHeader("X-Role") String role) {

        if (role == null || !role.equals("MASTER")){
            throw new SecurityException("접근 권한이 없습니다.");
        }
        HubResponseDto responseDto = hubService.createHub(requestDto.toDto(), username);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @GetMapping("/{hubId}/company")
    public ResponseEntity<ApiResponseDto<HubResponseDto>> findByHubIdAndCompany(@PathVariable("hubId") UUID hubId,
                                                                      @RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10")int size,
                                                                                @RequestHeader("X-Role") String role){
        if (role.isEmpty()){
            throw new SecurityException("접근 권한이 필요합니다.");
        }

        HubResponseDto responseDto = hubService.findByHubIdAndCompany(hubId, page-1, size);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }
  
    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponseDto<HubResponseDto>> findByHubId(@PathVariable("hubId") UUID hubId){


        HubResponseDto responseDto = hubService.findByHubId(hubId);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }
    @GetMapping
    public ResponseEntity<ApiResponseDto<PageResponseDto<HubResponseDto>>> findByAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10")int size
            ){

//        if (role.isEmpty()){
//            throw new SecurityException("접근 권한이 필요합니다.");
//        }

        PageResponseDto<HubResponseDto> responseDto = hubService.findByAll(page-1, size);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @PutMapping("/{hubId}")
    public ResponseEntity<ApiResponseDto<HubResponseDto>> updateHub(@PathVariable("hubId") UUID hubId,
                                                                    @RequestBody HubRequestDto requestDto,
                                                                    @RequestHeader("X-Username") String username,
                                                                    @RequestHeader("X-Role") String role){

        if (role == null || !role.equals("MASTER")){
            throw new SecurityException("접근 권한이 없습니다.");
        }
        HubResponseDto responseDto = hubService.updateHub(hubId, requestDto.toDto(), username);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @DeleteMapping("/{hubId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteHub(@PathVariable("hubId") UUID hubId,
                                                          @RequestHeader("X-Username") String username,
                                                          @RequestHeader("X-Role") String role){
        if (role == null || !role.equals("MASTER")){
            throw new SecurityException("접근 권한이 없습니다.");
        }
        hubService.deleteHub(hubId, username);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(null, "요청이 성공적으로 처리되었습니다."));
    }

}
