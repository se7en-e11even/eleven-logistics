package com.eleven.logistics.product.application;

import com.eleven.logistics.product.application.dto.CreateDto;
import com.eleven.logistics.product.application.dto.ResponseDto;
import com.eleven.logistics.product.application.dto.UpdateDto;
import com.eleven.logistics.product.common.exception.CustomException;
import com.eleven.logistics.product.common.resolver.dto.PageRequestDto;
import com.eleven.logistics.product.common.resolver.dto.PageResponseDto;
import com.eleven.logistics.product.domain.entity.Product;
import com.eleven.logistics.product.domain.repository.ProductRepository;
import com.eleven.logistics.product.infrastructure.ProductRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.eleven.logistics.product.domain.exception.ProductErrorCode.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductRepositoryCustom repositoryCustom;


    @Transactional
    public UUID createProduct(CreateDto dto) {
        // 엔티티에 객체 생성에 대한 책임을 부여한다.
        Product product = Product.builder()
                .companyId(dto.companyId())
                .hubId(dto.hubId())
                .name(dto.name())
                .price(dto.price())
                .quantity(dto.quantity())
                .build();

        repository.save(product);

        // Dirty Checking 으로 productId 가 넣어진다.
        return product.getProductId();
    }

    @Transactional(readOnly = true)
    public PageResponseDto<ResponseDto> readProducts(PageRequestDto pageRequestDto) {
        return repositoryCustom.readProducts(pageRequestDto);
    }

    public ResponseDto readProduct(UUID productId) {
        return repository.findByProductIdAndDeletedAtIsNull(productId)
                .map(ResponseDto::of)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }

    @Transactional
    public void updateProduct(UpdateDto dto) {
        Product updateProduct = repository.findByProductIdAndDeletedAtIsNull(dto.productId())
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        updateProduct.updateOf(
                dto.companyId(),
                dto.hubId(),
                dto.name(),
                dto.price(),
                dto.quantity()
        );
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        Product deleteProduct = repository.findByProductIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
        // TODO: 삭제자 정보 가져오기
        deleteProduct.deleteOf("userId");
    }

    @Transactional(readOnly = true)
    public PageResponseDto<ResponseDto> searchProducts(
            String keyword,
            PageRequestDto pageRequestDto
    ) {
        return repositoryCustom.retrieveProducts(keyword, pageRequestDto);
    }
}
