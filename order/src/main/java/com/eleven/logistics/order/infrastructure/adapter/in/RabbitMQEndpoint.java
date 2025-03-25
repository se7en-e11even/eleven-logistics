package com.eleven.logistics.order.infrastructure.adapter.in;

import com.eleven.logistics.order.application.dto.message.FromDelivery;
import com.eleven.logistics.order.application.dto.message.FromDeliveryError;
import com.eleven.logistics.order.application.service.OrderService;
import com.eleven.logistics.order.domain.entity.Order;
import com.eleven.logistics.order.domain.repository.OrderRepository;
import com.eleven.logistics.order.domain.vo.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEndpoint {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @RabbitListener(queues = "${message.receive.queue.delivery}")
    public void fromDelivery(FromDelivery message) {
        log.info("receive message: {}", message);

        // message.status() APPROVED | DELIVERING | COMPLETED
        Order byOrderId = orderRepository.findByOrderId(message.orderId());
        if ("APPROVED".equals(message.status())) {
            byOrderId.updateDeliveryId(message.deliveryId());
            byOrderId.changeOrderStatus(OrderStatus.valueOf(message.status()));
        }
        orderRepository.save(byOrderId);
    }

    @RabbitListener(queues = "${message.receive.err.queue.order}")
    public void fromDeliveryError(FromDeliveryError message) {
        log.info("receive error message: {}", message);

        if ("ERROR".equals(message.message())) {
            // 주문을 롤백해야 한다.
            orderService.rollback(message.orderId());
        } else {
            log.info("receive error message: {}", message.message());
        }
    }
}
