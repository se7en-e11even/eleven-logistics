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
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 상품 생성 API
     */
    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody @Valid CreateRequestDto requestDto) {
        // 업체 아이디, 허브 아이디를 사용자가 로그인 했을 때 그 사용자의 소속 회사와 회사 소속 허브를 가져오는 것이 맞을까?
        // 상품 등록 요청에 직접 넣어 요청 하도록 하는 것이 맞을까?
        UUID productId = productService.createProduct(requestDto.toDto());

        URI location = UriComponentsBuilder.newInstance()
                .path("/api/products/{product_id}")
                .buildAndExpand(productId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * 상품 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<ResponseDto>> readProducts(@RequestParam(defaultValue = "1") int page,
                                                                     @PageSize int size,
                                                                     @RequestParam(defaultValue = "desc") String orderBy
    ) {
        log.info("pageSize: {}", size);
        PageRequestDto pageRequestDto = PageRequestDto.of(page-1, size, orderBy);
        return ResponseEntity.ok(productService.readProducts(pageRequestDto));
    }

    /**
     * 상품 상세 조회 API
     */
    @GetMapping("/{product_id}")
    public ResponseEntity<?> readProduct(@PathVariable UUID product_id) {
        return ResponseEntity.ok(productService.readProduct(product_id));
    }

    /**
     * 상품 수정 API
     */
    @PutMapping("/{product_id}")
    public ResponseEntity<Void> updateProduct(@PathVariable UUID product_id,
                                              @RequestBody @Valid UpdateRequestDto requestDto
    ) {
        // service 에 controller 의 의존성을 없애기 위해 dto 변환
        UpdateDto updateDto = requestDto.withId(product_id);
        productService.updateProduct(updateDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 삭제 API
     */
    @DeleteMapping("/{product_id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID product_id) {
        // TODO: 삭제자 정보 가져와서 넘겨주기
        productService.deleteProduct(product_id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 검색 API
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<ResponseDto>> searchProducts(
            @RequestParam @NotBlank(message = "검색어를 입력해주세요.") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @PageSize int size,
            @RequestParam(defaultValue = "desc") String orderBy
    ) {
        PageRequestDto pageRequestDto = PageRequestDto.of(page-1, size, orderBy);
        return ResponseEntity.ok(productService.searchProducts(keyword, pageRequestDto));
    }
}
