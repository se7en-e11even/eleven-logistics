package com.eleven.logistics.hub.presentation.controller.company;

import com.eleven.logistics.common.dto.ApiResponseDto;

import com.eleven.logistics.hub.application.dto.PageResponseDto;
import com.eleven.logistics.hub.application.dto.company.CompanyResponseDto;
import com.eleven.logistics.hub.application.service.company.CompanyService;
import com.eleven.logistics.hub.presentation.dto.company.CompanyRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyService companyService;
    private final HttpServletRequest request;

    @PostMapping
    public ResponseEntity<ApiResponseDto<CompanyResponseDto>> createCompany(@RequestBody CompanyRequestDto requestDto,
                                                                            @RequestHeader("X-Username") String username) {
        String role =  request.getHeader("X-Role");
        if (role == null || !role.equals("MASTER") && !role.equals("HUB")) {
            throw new SecurityException("접근 권한이 없습니다.");
        }

        CompanyResponseDto responseDto = companyService.createCompany(username,requestDto.toDto());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponseDto<CompanyResponseDto>> findByCompanyId(@PathVariable("companyId") UUID companyId){
        String role =  request.getHeader("X-Role");
        if (role == null){
            throw new SecurityException("접근 권한이 없습니다.");
        }
        CompanyResponseDto responseDto = companyService.findByCompanyId(companyId);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponseDto<CompanyResponseDto>> findByCompanyUsername(@PathVariable("username") String username){
        String role =  request.getHeader("X-Role");
        if (role == null){
            throw new SecurityException("접근 권한이 없습니다.");
        }
        CompanyResponseDto responseDto = companyService.findByCompanyUsername(username);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<PageResponseDto<CompanyResponseDto>>> findByAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10")int size){
        String role =  request.getHeader("X-Role");
        if (role == null){
            throw new SecurityException("접근 권한이 없습니다.");
        }
        PageResponseDto<CompanyResponseDto> responseDto = companyService.findByAll(page-1, size);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<ApiResponseDto<CompanyResponseDto>> updateCompany(@PathVariable("companyId") UUID companyId,
                                                                    @RequestBody CompanyRequestDto requestDto,
                                                                            @RequestHeader("X-Username") String username) {

        String role =  request.getHeader("X-Role");
        if (role == null || role.equals("DELIVERY")){
            throw new SecurityException("접근 권한이 없습니다.");
        }
        CompanyResponseDto responseDto = companyService.updateCompany(companyId, requestDto.toDto(), username);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(responseDto, "요청이 성공적으로 처리되었습니다."));
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteCompany(@PathVariable("companyId") UUID companyId,
                                                              @RequestHeader("X-Username") String username) {
        String role =  request.getHeader("X-Role");
        if (role == null || !role.equals("MASTER") && !role.equals("COMPANY")){
            throw new SecurityException("접근 권한이 없습니다.");
        }
        companyService.deleteCompany(companyId, username);
        return ResponseEntity.ok()
                .body(ApiResponseDto.success(null, "요청이 성공적으로 처리되었습니다."));
    }
}
