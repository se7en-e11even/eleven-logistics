package com.eleven.logistics.slack.application.external;

import com.eleven.logistics.slack.application.orderProduct.FindProductQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface ProductService {

    ResponseEntity<FindProductQuery> read(@PathVariable UUID product_id,
                                          @RequestHeader("X-Username") String username,
                                          @RequestHeader("X-Role") String role);
}
