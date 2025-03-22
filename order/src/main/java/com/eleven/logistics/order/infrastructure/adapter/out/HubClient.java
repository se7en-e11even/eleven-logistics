package com.eleven.logistics.order.infrastructure.adapter.out;

import com.eleven.logistics.order.application.dto.query.FindHubQuery;
import com.eleven.logistics.order.application.port.out.HubPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hub-service", contextId = "hubClient")
public interface HubClient extends HubPort {

    @Override
    @GetMapping("/api/hub/{hub_id}")
    FindHubQuery getHubByHubId(
            @PathVariable String hub_id,
            @RequestParam(defaultValue = "1") int size,
            @RequestParam(defaultValue = "10") int pageSize
    );
}
