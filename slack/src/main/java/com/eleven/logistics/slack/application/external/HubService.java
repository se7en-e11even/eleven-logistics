package com.eleven.logistics.slack.application.external;

import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.slack.application.companydto.CompanyResponseDto;
import com.eleven.logistics.slack.application.hubdto.HubResponseDto;
import com.eleven.logistics.slack.application.dto.PageResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface HubService {

    ResponseEntity<ApiResponseDto<HubResponseDto>> findByHubId(@PathVariable("hubId") UUID hubId);
}