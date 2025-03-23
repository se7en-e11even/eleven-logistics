package com.eleven.logistics.order.application.port.out;

import com.eleven.logistics.order.application.dto.command.ProductOrderCommand;
import com.eleven.logistics.order.application.dto.query.FindProductQuery;

public interface ProductPort {

    FindProductQuery getProductByProductId(String productId);

    void putProductOrder(ProductOrderCommand orderProduct);
}
