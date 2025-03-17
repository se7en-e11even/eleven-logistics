package com.eleven.logistics.hub.presentation.controller.company;

import com.eleven.logistics.common.dto.ApiResponseDto;

import com.eleven.logistics.hub.application.dto.company.CompanyResponseDto;
import com.eleven.logistics.hub.application.service.company.CompanyService;
import com.eleven.logistics.hub.presentation.dto.company.CompanyRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/company")
    public ResponseEntity<ApiResponseDto<CompanyResponseDto>> createCompany(@RequestBody CompanyRequestDto requestDto){
        CompanyResponseDto responseDto = companyService.createCompany(requestDto.toDto());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponseDto<CompanyResponseDto>> findByCompanyId(@PathVariable("companyId") UUID companyId){
        CompanyResponseDto responseDto = companyService.findByCompanyId(companyId);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @GetMapping("/company")
    public ResponseEntity<ApiResponseDto<Page<CompanyResponseDto>>> findByAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10")int size){
        Page<CompanyResponseDto> responseDto = companyService.findByAll(page-1, size);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @PutMapping("/company/{companyId}")
    public ResponseEntity<ApiResponseDto<CompanyResponseDto>> updateCompany(@PathVariable("companyId") UUID companyId,
                                                                    @RequestBody CompanyRequestDto requestDto){
        CompanyResponseDto responseDto = companyService.updateCompany(companyId, requestDto.toDto());
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @DeleteMapping("/company/{companyId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteCompany(@PathVariable("companyId") UUID companyId){
        companyService.deleteCompany(companyId);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(null, "요청이 성공적으로 처리되었습니다."));
    }
}
