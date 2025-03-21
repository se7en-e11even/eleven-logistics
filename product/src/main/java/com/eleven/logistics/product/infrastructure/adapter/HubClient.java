package com.eleven.logistics.product.infrastructure.adapter;

import com.eleven.logistics.product.application.dto.query.FindHubQuery;
import com.eleven.logistics.product.application.port.out.HubPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service", contextId = "hubClient")
public interface HubClient extends HubPort {

    @Override
    @GetMapping("/api/hub/{hub_id}")
    FindHubQuery getHubByHubId(@PathVariable String hub_id);
}
