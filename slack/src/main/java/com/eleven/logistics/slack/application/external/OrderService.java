package com.eleven.logistics.slack.application.external;

import com.eleven.logistics.slack.application.orderProduct.FindOrderQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface OrderService {
    ResponseEntity<FindOrderQuery> read(@PathVariable UUID order_id, @RequestHeader String username, @RequestHeader String role);
}