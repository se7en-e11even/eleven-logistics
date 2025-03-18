package com.eleven.logistics.order.application;

import com.eleven.logistics.order.application.dto.CreateDto;
import com.eleven.logistics.order.application.dto.OrderProductDto;
import com.eleven.logistics.order.application.dto.ResponseDto;
import com.eleven.logistics.order.application.dto.UpdateDto;
import com.eleven.logistics.order.common.exception.CustomException;
import com.eleven.logistics.order.common.resolver.dto.PageRequestDto;
import com.eleven.logistics.order.common.resolver.dto.PageResponseDto;
import com.eleven.logistics.order.domain.entity.Order;
import com.eleven.logistics.order.domain.entity.OrderProduct;
import com.eleven.logistics.order.domain.entity.OrderStatus;
import com.eleven.logistics.order.domain.repository.OrderRepository;
import com.eleven.logistics.order.infrastructure.OrderRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static com.eleven.logistics.order.domain.exception.OrderErrorCode.ORDER_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderRepositoryCustom repositoryCustom;

    public UUID createOrder(CreateDto dto) {
        // 저장할 엔티티 생성
        // 생산자, 수신자 아이디를 생성 요청 시 컨트롤러에서 받아오는 것 보다
        // product, 주문자 아이디를 통해 각 서비스에 요청하는 것이 맞을 것 같다..
        // 배송 id 는 배송 서비스에서 받아와야 한다.

        // 주문 생성
        Order order = Order.builder()
                .supplyId(dto.supplyId())
                .receiverId(dto.receiverId())
                .orderStatus(OrderStatus.PENDING)
                .request(dto.request())
                .orderProductList(new ArrayList<>())
                .build();

        // 주문 상품 추가
        for (OrderProductDto orderProductDto : dto.orderProductDtoList()) {
            OrderProduct orderProduct = OrderProduct.builder()
                    .productId(orderProductDto.productId())
                    .price(orderProductDto.price())
                    .quantity(orderProductDto.quantity())
                    .build();
            order.addOrderProduct(orderProduct);
        }
        repository.save(order);
        return order.getOrderId();
    }

    @Transactional(readOnly = true)
    public PageResponseDto<ResponseDto> readOrders(PageRequestDto pageRequestDto) {
        return repositoryCustom.readOrders(pageRequestDto);
    }

    @Transactional(readOnly = true)
    public ResponseDto readOrder(UUID orderId) {
        return repositoryCustom.findById(orderId)
                .map(ResponseDto::of)
                .orElseThrow(()->new CustomException(ORDER_NOT_FOUND));
    }

    public void updateOrder(UpdateDto updateDto) {
        Order fetchedOrder = repositoryCustom.findById(updateDto.orderId())
                        .orElseThrow(()->new CustomException(ORDER_NOT_FOUND));

        fetchedOrder.updateOf(
                updateDto.request()
        );

        // TODO: updateDto 의 orderProductList 수정
    }

    // 주문 취소, 주문 상태를 CANCELED 로 변경하여 정보를 저장한다.
    public ResponseDto cancelOrder(UUID orderId) {
        Order order = repositoryCustom.findById(orderId)
                        .orElseThrow(()->new CustomException(ORDER_NOT_FOUND));
        order.changeOrderStatus("CANCELED");
        return ResponseDto.of(order);
    }

    public void deleteOrder(UUID orderId) {
        Order order = repository.findByOrderId(orderId);

        // TODO: 사용자 정보 넣기
        order.deleteOf("userId");
    }

    // 주문한 상품 정보 삭제
    public void deleteOrderProducts(UUID orderId, UUID orderProductId) {
        // 주문 정보에서 주문상품 정보를 불러와서 ...
//        OrderProduct orderProduct;
//        orderProduct.deleteOf("user");
    }

    @Transactional(readOnly = true)
    public PageResponseDto<ResponseDto> searchProducts(
            String keyword,
            PageRequestDto pageRequestDto
    ) {
        return repositoryCustom.retrieveOrders(keyword, pageRequestDto);
    }
}
