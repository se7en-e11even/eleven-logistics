package com.eleven.logistics.order.infrastructure.adapter.out;

import com.eleven.logistics.order.application.dto.query.FindCompanyQuery;
import com.eleven.logistics.order.application.port.out.CompanyPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service", contextId = "companyClient")
public interface CompanyClient extends CompanyPort {

    @Override
    @GetMapping("/api/company/{companyId}")
    FindCompanyQuery getCompanyByCompanyId(@PathVariable String companyId);

    @Override
    @GetMapping("/api/company/username/{username}")
    FindCompanyQuery getCompanyByUsername(@PathVariable String username);
}
