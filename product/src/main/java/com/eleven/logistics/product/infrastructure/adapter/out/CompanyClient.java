package com.eleven.logistics.product.infrastructure.adapter.out;

import com.eleven.logistics.product.application.dto.query.FindCompanyQuery;
import com.eleven.logistics.product.application.port.out.CompanyPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * application 계층의 CompanyPort 를 @FeignClient 에서 구현하도록 CompanyPort 를 통해 추상화한다.
 * hub-service 에서 company 도 함께 관리한다.
 * 유레카 서비스를 사용 중이므로 url 정보는 입력하지 않음
 *
 * hub-service 에서 company 도메인과 hub 도메인을 함께 관리하므로 구분 하기 위해 contextId를 사용하여
 * FeignClient 가 서로 다른 빈으로 등록할 수 있도록 함.
 */
@FeignClient(name = "hub-service", contextId = "companyClient")
public interface CompanyClient extends CompanyPort {

    @Override
    @GetMapping("/api/company/username/{username}")
    FindCompanyQuery getCompanyByUsername(@PathVariable String username);
}
