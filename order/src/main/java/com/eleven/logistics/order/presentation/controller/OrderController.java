package com.eleven.logistics.order.presentation.controller;

import com.eleven.logistics.order.application.dto.query.FindOrderQuery;
import com.eleven.logistics.order.application.service.OrderService;
import com.eleven.logistics.order.presentation.dto.request.CreateOrderRequest;
import com.eleven.logistics.order.presentation.dto.request.UpdateOrderRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Void> create(
            @RequestBody @Valid CreateOrderRequest request
    ) {
        // 주문 생성 시 배송이 함께 생성 되어야 함! 배송 정보 확인 후 status 를 APPROVED 로 할 것!
        UUID orderId = orderService.create(request.toCommand());

        URI location = UriComponentsBuilder.newInstance()
                .path("/api/orders/{order_id}")
                .buildAndExpand(orderId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * 주문 상세 조회 API
     */
    @GetMapping("/{order_id}")
    public ResponseEntity<?> read(@PathVariable UUID order_id) {
        return ResponseEntity.ok(orderService.read(order_id));
    }

    /**
     * 주문 수정 API
     */
    @PutMapping("/{order_id}")
    public ResponseEntity<Void> update(
            @PathVariable UUID order_id,
            @RequestBody @Valid UpdateOrderRequest request
    ) {
        orderService.update(request.toCommandWithId(order_id));
        return ResponseEntity.noContent().build();
    }

    /**
     * 주문 취소 API
     * 주문 당사자가 취소 했을 때는 주문 상태만 변경한다.
     * 취소된 주문도 검색이 가능하며 전체 목록에도 보인다.
     */
    @PatchMapping("/{order_id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable UUID order_id) {
        return ResponseEntity.ok(orderService.cancel(order_id));
    }

    /**
     * 주문 삭제 API
     * 삭제 요청을 했을 때에만 deletedAt 을 업데이트 한다.
     */
    @DeleteMapping("/{order_id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID order_id,
            @RequestHeader("X-Username") String username
    ) {
        orderService.delete(order_id, username);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 검색 API
     */
    @GetMapping
    public ResponseEntity<Page<FindOrderQuery>> search(
            @RequestParam(defaultValue = "") String keyword,
            Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.search(keyword, pageable));
    }
}
