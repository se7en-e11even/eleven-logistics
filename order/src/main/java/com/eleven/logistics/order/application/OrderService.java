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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static com.eleven.logistics.order.domain.exception.OrderErrorCode.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;

    @Transactional
    public UUID createOrder(CreateDto dto) {
        // 저장할 엔티티 생성
        // 생산자, 수신자 아이디를 생성 요청 시 컨트롤러에서 받아오는 것 보다
        // product, 주문자 아이디를 통해 각 서비스에 요청하는 것이 맞을 것 같다..
        // 배송 id 는 배송 서비스에서 받아와야 한다.
        // 상품 정보를 주문에 넣어야 하므로 상품 먼저 생성

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
        return null;
    }

    @Transactional(readOnly = true)
    public ResponseDto readOrder(UUID orderId) {
        return repository.findByOrderIdAndDeletedAtIsNull(orderId)
                .map(ResponseDto::of)
                .orElseThrow(()->new CustomException(ORDER_NOT_FOUND));
    }

    @Transactional
    public void updateOrder(UpdateDto updateDto) {

    }

    @Transactional
    public void deleteOrder(UUID orderId) {

    }

    @Transactional(readOnly = true)
    public PageResponseDto<ResponseDto> searchProducts(
            String keyword,
            PageRequestDto pageRequestDto
    ) {
        return null;
    }
}
