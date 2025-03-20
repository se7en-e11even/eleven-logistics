package com.eleven.logistics.order.application.service;

import com.eleven.logistics.order.application.dto.command.CreateOrderCommand;
import com.eleven.logistics.order.application.dto.command.CreateOrderProductCommand;
import com.eleven.logistics.order.application.dto.command.ListOrderCommand;
import com.eleven.logistics.order.application.dto.command.UpdateOrderCommand;
import com.eleven.logistics.order.application.dto.query.FindOrderQuery;
import com.eleven.logistics.order.application.dto.query.FindProductQuery;
import com.eleven.logistics.order.application.dto.query.ListOrderQuery;
import com.eleven.logistics.order.application.port.out.ProductPort;
import com.eleven.logistics.order.common.exception.CustomException;
import com.eleven.logistics.order.domain.entity.Order;
import com.eleven.logistics.order.domain.entity.OrderProduct;
import com.eleven.logistics.order.domain.repository.OrderRepository;
import com.eleven.logistics.order.domain.repository.OrderRepositoryCustom;
import com.eleven.logistics.order.domain.vo.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static com.eleven.logistics.order.domain.exception.OrderErrorCode.ORDER_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderRepositoryCustom repositoryCustom;
    private final ProductPort productPort;

    public UUID create(CreateOrderCommand command) {
        // feign client 요청 테스트, 상품 id를 통해 공급업체 id, 상품 재고를 알 수 있다.
        // 주문 수량과 재고를 비교해야 한다.
        FindProductQuery product = productPort.getProduct(command.commandList().get(0).productId().toString());

        log.info("productDto = {}", product);

        // 생산자, 수신자 아이디를 생성 요청 시 컨트롤러에서 받아오는 것 보다
        // product, 주문자 아이디를 통해 각 서비스에 요청하는 것이 맞을 것 같다..
        // 배송 id 는 배송 서비스에서 받아와야 한다.

        // 저장할 주문 엔티티 생성
        Order order = Order.builder()
                .supplyId(command.supplyId())
                .receiverId(command.receiverId())
                .orderStatus(OrderStatus.PENDING)
                .request(command.request())
                .orderProductList(new ArrayList<>())
                .build();

        // 주문 상품 추가
        for (CreateOrderProductCommand orderProductCommand : command.commandList()) {
            OrderProduct orderProduct = OrderProduct.builder()
                    .productId(orderProductCommand.productId())
                    .price(orderProductCommand.price())
                    .quantity(orderProductCommand.quantity())
                    .build();
            order.addOrderProduct(orderProduct);
        }
        repository.save(order);
        return order.getOrderId();
    }

    @Transactional(readOnly = true)
    public FindOrderQuery read(UUID orderId) {
        return repositoryCustom.findById(orderId)
                .map(FindOrderQuery::of)
                .orElseThrow(()->new CustomException(ORDER_NOT_FOUND));
    }

    public void update(UpdateOrderCommand command) {
        Order order = repositoryCustom.findById(command.orderId())
                        .orElseThrow(()->new CustomException(ORDER_NOT_FOUND));

        order.updateOf(
                command.request()
        );

        // TODO: updateDto 의 orderProductList 수정
    }

    // 주문 취소, 주문 상태를 CANCELED 로 변경하여 정보를 저장한다.
    public FindOrderQuery cancel(UUID orderId) {
        Order order = repositoryCustom.findById(orderId)
                        .orElseThrow(()->new CustomException(ORDER_NOT_FOUND));
        order.changeOrderStatus("CANCELED");
        return FindOrderQuery.of(order);
    }

    public void delete(UUID orderId, String username) {
        Order order = repository.findByOrderId(orderId);

        // TODO: 사용자 정보 넣기
        order.deleteOf(username);
    }

    // 주문한 상품 정보 삭제
    public void deleteOrderProducts(UUID orderId, UUID orderProductId) {
        // 주문 정보에서 주문상품 정보를 불러와서 ...
//        OrderProduct orderProduct;
//        orderProduct.deleteOf("user");
    }

    @Transactional(readOnly = true)
    public ListOrderQuery<FindOrderQuery> search(
            String keyword,
            ListOrderCommand command
    ) {
        return repositoryCustom.retrieve(keyword, command);
    }
}
