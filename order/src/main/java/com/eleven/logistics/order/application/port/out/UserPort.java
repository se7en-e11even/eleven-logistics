package com.eleven.logistics.order.application.port.out;

import com.eleven.logistics.order.application.dto.query.FindUserQuery;

public interface UserPort {

    FindUserQuery getUserByUsername(String username);
}
