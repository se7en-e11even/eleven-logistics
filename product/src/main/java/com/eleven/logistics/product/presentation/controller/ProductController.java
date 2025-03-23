package com.eleven.logistics.product.presentation.controller;

import com.eleven.logistics.product.application.dto.command.CreateProductCommand;
import com.eleven.logistics.product.application.dto.query.FindProductQuery;
import com.eleven.logistics.product.application.service.ProductService;
import com.eleven.logistics.product.presentation.dto.request.CreateProductRequest;
import com.eleven.logistics.product.presentation.dto.request.OrderProductRequest;
import com.eleven.logistics.product.presentation.dto.request.UpdateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "Product Controller")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 생성 API
     */
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody @Valid CreateProductRequest request,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role
    ) {
        // 배송 담당자는 상품을 추가할 수 없다.
        if ("DELIVERY".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "상품 생성 권한이 없습니다."
            );
        }

        // service 에 controller 의 의존성을 없애기 위해 dto 변환
        CreateProductCommand command = request.toCommand();
        UUID productId = productService.create(command, username, role).productId();

        URI location = UriComponentsBuilder.newInstance()
                .path("/api/products/{product_id}")
                .buildAndExpand(productId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * 상품 조회 API
     */
    @GetMapping("/{product_id}")
    public ResponseEntity<?> read(
            @PathVariable UUID product_id,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role
    ) {
        return ResponseEntity.ok(productService.read(product_id, username, role));
    }

    /**
     * 상품 수정 API
     */
    @PutMapping("/{product_id}")
    public ResponseEntity<Void> update(
            @PathVariable UUID product_id,
            @RequestBody @Valid UpdateProductRequest request,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role
    ) {
        // 배송 담당자는 상품을 수정할 수 없다.
        if ("DELIVERY".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "상품 수정 권한이 없습니다."
            );
        }

        // service 에 controller 의 의존성을 없애기 위해 dto 변환
        productService.update(request.toCommand(product_id), username, role);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 삭제 API
     */
    @DeleteMapping("/{product_id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID product_id,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role
    ) {
        // 배송 담당자와 업체 담당자는 상품을 삭제할 수 없다.
        if ("DELIVERY".equals(role) || "COMPANY".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "상품 삭제 권한이 없습니다."
            );
        }
        // 허브 담당자는 담당 허브의 상품만 삭제할 수 있다.
        productService.delete(product_id, username, role);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 검색 API
     * 페이징 처리, keyword 가 없으면 권한에 맞는 전체 목록
     */
    @GetMapping
    public ResponseEntity<Page<FindProductQuery>> search(
            @RequestParam(defaultValue = "") String keyword,
            Pageable pageable,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role
    ) {
        log.info("pageSize 검증 10, 30, 50(default 10): {}", pageable.getPageSize());
        return ResponseEntity.ok(productService.search(keyword, pageable, username, role));
    }

    /**
     * Order Service 의 주문 API
     */
    @PutMapping("/orders")
    public ResponseEntity<Void> orders(
            @RequestBody OrderProductRequest orderRequest
    ) {
        productService.orders(orderRequest.toCommand());
        return ResponseEntity.noContent().build();
    }
}
