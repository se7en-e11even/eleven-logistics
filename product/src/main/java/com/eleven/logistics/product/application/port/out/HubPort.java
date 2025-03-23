package com.eleven.logistics.product.application.port.out;

import com.eleven.logistics.product.application.dto.query.FindHubQuery;

public interface HubPort {
    FindHubQuery getHubByHubId(String HubId, int page, int pageSize);
}
