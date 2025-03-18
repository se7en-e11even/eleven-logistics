package com.eleven.logistics.order.application;

import com.eleven.logistics.order.application.dto.CreateDto;
import com.eleven.logistics.order.application.dto.OrderProductDto;
import com.eleven.logistics.order.application.dto.ResponseDto;
import com.eleven.logistics.order.application.dto.UpdateDto;
import com.eleven.logistics.order.common.resolver.dto.PageRequestDto;
import com.eleven.logistics.order.common.resolver.dto.PageResponseDto;
import com.eleven.logistics.order.domain.entity.Order;
import com.eleven.logistics.order.domain.entity.OrderProduct;
import com.eleven.logistics.order.domain.entity.OrderStatus;
import com.eleven.logistics.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

        // 계층간 역 의존성을 방지하기 위해 application 계층에도 dto...
        List<OrderProductDto> orderProductDtoList = dto.orderProductDtoList();
        List<OrderProduct> orderProductList = orderProductDtoList.stream()
                .map(OrderProductDto::toEntity)
                .toList();
        Order order = Order.builder()
                .supplyId(dto.supplyId())
                .receiverId(dto.receiverId())
                .orderStatus(OrderStatus.PENDING)
                .request(dto.request())
                .orderProductList(orderProductList)
                .build();

        repository.save(order);
        return order.getOrderId();
    }

    @Transactional(readOnly = true)
    public PageResponseDto<ResponseDto> readOrders(PageRequestDto pageRequestDto) {
        return null;
    }

    public Object readOrder(UUID orderId) {
        return null;
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
