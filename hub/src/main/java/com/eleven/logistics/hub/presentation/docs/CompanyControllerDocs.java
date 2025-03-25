package com.eleven.logistics.hub.presentation.docs;

import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.hub.application.dto.PageResponseDto;
import com.eleven.logistics.hub.application.dto.company.CompanyResponseDto;
import com.eleven.logistics.hub.presentation.dto.company.CompanyRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "업체", description = "업체 생성, 검색, 수정, 삭제 등의 API")
public interface CompanyControllerDocs {

    @PostMapping("/api/company")
    @Operation(summary = "업체 생성", description = "업체를 생성하는 API 입니다.")
    ResponseEntity<ApiResponseDto<CompanyResponseDto>> createCompany(@RequestBody CompanyRequestDto requestDto,
                                                                     @RequestHeader("X-Username") String username,
                                                                     @RequestHeader("X-Role") String role);

    @GetMapping("/api/company/{companyId}")
    @Operation(summary = "업체 단건 조회", description = "업체를 단건 조회하는 API 입니다.")
    ResponseEntity<ApiResponseDto<CompanyResponseDto>> findByCompanyId(@PathVariable("companyId") UUID companyId,
                                                                              @RequestHeader("X-Role") String role);

    @GetMapping("/api/company/username/{username}")
    @Operation(summary = "업체 전체 조회", description = "업체를 전체 조회하는 API 입니다.")
    ResponseEntity<ApiResponseDto<CompanyResponseDto>> findByCompanyUsername(@PathVariable("username") String username,
                                                                                    @RequestHeader("X-Role") String role);



    @GetMapping("/api/company")
    @Operation(summary = "업체 전체 검색", description = "전체 업체를 검색하는 API 입니다.")
    ResponseEntity<ApiResponseDto<PageResponseDto<CompanyResponseDto>>> findByAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size);



    @PutMapping("/api/company/{companyId}")
    @Operation(summary = "업체 수정", description = "업체를 수정하는 API 입니다.")
    ResponseEntity<ApiResponseDto<CompanyResponseDto>> updateCompany(@PathVariable("companyId") UUID companyId,
                                                             @RequestBody CompanyRequestDto requestDto,
                                                             @RequestHeader("X-Username") String username,
                                                             @RequestHeader("X-Role") String role);

    @DeleteMapping("/api/company/{companyId}")
    @Operation(summary = "업체 삭제", description = "업체를 삭제하는 API 입니다.")
    ResponseEntity<ApiResponseDto<Void>> deleteCompany(@PathVariable("companyId") UUID companyId,
                                                   @RequestHeader("X-Username") String username,
                                                   @RequestHeader("X-Role") String role);
}