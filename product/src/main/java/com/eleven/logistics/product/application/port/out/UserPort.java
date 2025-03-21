package com.eleven.logistics.product.application.port.out;

import com.eleven.logistics.product.application.dto.query.FindUserQuery;

public interface UserPort {
    FindUserQuery getUserByUsername(String username);
}
