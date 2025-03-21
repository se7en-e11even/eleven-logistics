package com.eleven.logistics.slack.application.external;

import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.slack.application.companydto.CompanyResponseDto;
import com.eleven.logistics.slack.application.hubdto.HubResponseDto;
import com.eleven.logistics.slack.application.dto.PageResponseDto;

public interface HubService {

    ApiResponseDto<PageResponseDto<HubResponseDto>> findByAllHub();

    ApiResponseDto<PageResponseDto<CompanyResponseDto>> findByAllCompany();
}
