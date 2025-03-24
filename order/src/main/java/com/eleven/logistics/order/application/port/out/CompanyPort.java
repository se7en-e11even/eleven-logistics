package com.eleven.logistics.order.application.port.out;

import com.eleven.logistics.order.application.dto.query.FindCompanyQuery;

public interface CompanyPort {

    FindCompanyQuery getCompanyByCompanyId(String companyId);

    FindCompanyQuery getCompanyByUsername(String username);
}
