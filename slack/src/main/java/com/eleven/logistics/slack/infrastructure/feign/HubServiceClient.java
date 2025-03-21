package com.eleven.logistics.slack.infrastructure.feign;

import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.slack.application.companydto.CompanyResponseDto;
import com.eleven.logistics.slack.application.hubdto.HubResponseDto;
import com.eleven.logistics.slack.application.dto.PageResponseDto;
import com.eleven.logistics.slack.application.external.HubService;
import com.eleven.logistics.slack.infrastructure.feign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service",configuration = FeignConfig.class)
public interface HubServiceClient extends HubService {

    @GetMapping("/api/hub/{hubId}")
    HubResponseDto findByHubId(@PathVariable("hubId") UUID hubId);

    @GetMapping("/api/hub")
    ApiResponseDto<PageResponseDto<HubResponseDto>> findByAllHub();

    @GetMapping("/api/company/{companyId}")
    ApiResponseDto<CompanyResponseDto> findByCompanyId(@PathVariable("companyId") UUID companyId);

    @GetMapping("/api/company")
    ApiResponseDto<PageResponseDto<CompanyResponseDto>> findByAllCompany();
}
