package com.eleven.logistics.order.application.port.out;

import com.eleven.logistics.order.application.dto.command.OrderProductCommand;
import com.eleven.logistics.order.application.dto.command.OrderRollbackCommand;
import com.eleven.logistics.order.application.dto.query.FindProductQuery;

public interface ProductPort {

    FindProductQuery getProductByProductId(String productId);

    void putProductOrder(OrderProductCommand orderProduct);

    void putProductRollBack(OrderRollbackCommand orderRollback);
}
