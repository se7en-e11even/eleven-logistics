package com.eleven.logistics.slack.application.external;

import com.eleven.logistics.slack.application.querydto.FindOrderQuery;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface OrderService {
    ResponseEntity<FindOrderQuery> read(UUID orderId);
}
