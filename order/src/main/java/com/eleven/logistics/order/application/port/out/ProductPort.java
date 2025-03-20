package com.eleven.logistics.order.application.port.out;

import com.eleven.logistics.order.application.dto.query.FindProductQuery;

public interface ProductPort {

    FindProductQuery getProduct(String productId);
}
