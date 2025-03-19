package com.eleven.logistics.product.presentation;

import com.eleven.logistics.product.application.ProductService;
import com.eleven.logistics.product.application.dto.ResponseDto;
import com.eleven.logistics.product.application.dto.UpdateDto;
import com.eleven.logistics.product.common.resolver.PageSize;
import com.eleven.logistics.product.common.resolver.dto.PageRequestDto;
import com.eleven.logistics.product.common.resolver.dto.PageResponseDto;
import com.eleven.logistics.product.presentation.dto.CreateRequestDto;
import com.eleven.logistics.product.presentation.dto.UpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "Controller")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // TODO: 허브 관리자는 담당 허브에 대해서만 cruds 가 가능하다.

    /**
     * 상품 생성 API
     */
    @PostMapping
    public ResponseEntity<Void> createProduct(
            @RequestBody @Valid CreateRequestDto requestDto,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role
    ) {
        // 배송 담당자는 상품을 추가할 수 없다.
        if (role.equals("DELIVERY")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // 허브 관리자는 허브 소속 회사의 상품만
        // if role.eq.HUB: username 으로 -> userid -> companyId, hubId

        // 업체 담당자는 해당 업체의 상품만 생성
        //  if role.eq.COMPANY: username -> userId -> companyId

        UUID productId = productService.createProduct(requestDto.toDto());

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
    public ResponseEntity<?> readProduct(@PathVariable UUID product_id) {
        return ResponseEntity.ok(productService.readProduct(product_id));
    }

    /**
     * 상품 수정 API
     */
    @PutMapping("/{product_id}")
    public ResponseEntity<Void> updateProduct(
            @PathVariable UUID product_id,
            @RequestBody @Valid UpdateRequestDto requestDto,
            @RequestHeader("X-Role") String role
    ) {
        // 배송 담당자는 상품을 수정할 수 없다.
        if (role.equals("DELIVERY")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // service 에 controller 의 의존성을 없애기 위해 dto 변환
        UpdateDto updateDto = requestDto.withId(product_id);
        productService.updateProduct(updateDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 삭제 API
     */
    @DeleteMapping("/{product_id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable UUID product_id,
            @RequestHeader("X-Username") String username
    ) {
        productService.deleteProduct(product_id, username);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 검색 API
     * 페이징 처리, keyword 가 없으면 권한에 맞는 전체 목록
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<ResponseDto>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @PageSize int size,
            @RequestParam(defaultValue = "desc") String orderBy
    ) {
        log.info("pageSize 검증 10, 30, 50(default 10): {}", size);
        PageRequestDto pageRequestDto = PageRequestDto.of(page-1, size, orderBy);
        return ResponseEntity.ok(productService.searchProducts(keyword, pageRequestDto));
    }
}
