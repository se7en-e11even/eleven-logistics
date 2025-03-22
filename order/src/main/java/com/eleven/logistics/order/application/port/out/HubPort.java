package com.eleven.logistics.order.application.port.out;

import com.eleven.logistics.order.application.dto.query.FindHubQuery;

public interface HubPort {
    FindHubQuery getHubByHubId(String HubId, int page, int pageSize);
}
