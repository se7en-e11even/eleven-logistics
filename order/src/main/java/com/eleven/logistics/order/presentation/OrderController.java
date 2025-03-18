package com.eleven.logistics.order.presentation;

import com.eleven.logistics.order.application.OrderService;
import com.eleven.logistics.order.application.dto.ResponseDto;
import com.eleven.logistics.order.application.dto.UpdateDto;
import com.eleven.logistics.order.common.resolver.PageSize;
import com.eleven.logistics.order.common.resolver.dto.PageRequestDto;
import com.eleven.logistics.order.common.resolver.dto.PageResponseDto;
import com.eleven.logistics.order.presentation.dto.CreateRequestDto;
import com.eleven.logistics.order.presentation.dto.UpdateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성 API
     */
    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody @Valid CreateRequestDto requestDto

    ) {
        // 주문 생성 시 배송이 함께 생성 되어야 함! 배송 정보 확인 후 status 를 APPROVED 로 할 것!
        UUID orderId = orderService.createOrder(requestDto.toDto());

        URI location = UriComponentsBuilder.newInstance()
                .path("/api/orders/{order_id}")
                .buildAndExpand(orderId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * 주문 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<ResponseDto>> readOrders(@RequestParam(defaultValue = "1") int page,
                                                                   @PageSize int size,
                                                                   @RequestParam(defaultValue = "desc") String orderBy
    ) {
        PageRequestDto pageRequestDto = new PageRequestDto(page - 1, size, orderBy);
        return ResponseEntity.ok(orderService.readOrders(pageRequestDto));
    }

    /**
     * 주문 상세 조회 API
     */
    @GetMapping("/{order_id}")
    public ResponseEntity<?> readOrder(@PathVariable UUID order_id) {
        return ResponseEntity.ok(orderService.readOrder(order_id));
    }

    /**
     * 주문 수정 API
     */
    @PutMapping("/{order_id}")
    public ResponseEntity<Void> updateOrder(@PathVariable UUID order_id,
                                            @RequestBody @Valid UpdateRequestDto requestDto
    ) {
        UpdateDto updateDto = requestDto.withId(order_id);
        orderService.updateOrder(updateDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 주문 취소 API
     * 주문 당사자가 취소 했을 때는 주문 상태만 변경한다.
     * 취소된 주문도 검색이 가능하며 전체 목록에도 보인다.
     */
    @PatchMapping("/{order_id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable UUID order_id) {
        return ResponseEntity.ok(orderService.cancelOrder(order_id));
    }

    /**
     * 주문 삭제 API
     * 삭제 요청을 했을 때에만 deletedAt 을 업데이트 한다.
     */
    @DeleteMapping("/{order_id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID order_id) {
        orderService.deleteOrder(order_id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 검색 API
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<ResponseDto>> searchOrders(
            @RequestParam @NotBlank(message = "검색어를 입력해주세요.") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @PageSize int size,
            @RequestParam(defaultValue = "desc") String orderBy
    ) {
        PageRequestDto pageRequestDto = new PageRequestDto(page - 1, size, orderBy);
        return ResponseEntity.ok(orderService.searchProducts(keyword, pageRequestDto));
    }
}
