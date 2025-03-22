package com.eleven.logistics.product.application.service;

import com.eleven.logistics.product.application.dto.command.CreateProductCommand;
import com.eleven.logistics.product.application.dto.command.UpdateProductCommand;
import com.eleven.logistics.product.application.dto.query.FindCompanyQuery;
import com.eleven.logistics.product.application.dto.query.FindHubQuery;
import com.eleven.logistics.product.application.dto.query.FindProductQuery;
import com.eleven.logistics.product.application.port.out.CompanyPort;
import com.eleven.logistics.product.application.port.out.HubPort;
import com.eleven.logistics.product.domain.entity.Product;
import com.eleven.logistics.product.domain.exception.CustomException;
import com.eleven.logistics.product.domain.repository.ProductRepository;
import com.eleven.logistics.product.domain.repository.ProductRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.eleven.logistics.product.domain.exception.ProductErrorCode.*;

@Slf4j(topic = "Product Service")
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductRepositoryCustom repositoryCustom;
    private final CompanyPort companyPort;
    private final HubPort hubPort;

    @CachePut(
            cacheNames = "productRead",
            key = "{ #result?.productId, #role == 'HUB' ? #role : '' }",
            unless = "#result == null"
    )
    public FindProductQuery create(CreateProductCommand command, String username, String role) {
        // 상품을 생성할 때 생성자가 속한 companyId, hubId를 받아와야 한다.
        Ids ids = getCompanyIdAndHubId(username);

        // 엔티티에 객체 생성에 대한 책임을 부여한다.
        Product product = Product.builder()
                .companyId(ids.companyId())
                .hubId(ids.hubId())
                .name(command.name())
                .price(command.price())
                .stockQuantity(command.quantity())
                .build();

        // 캐싱을 해야 해서 UUID -> DTO 를 리턴
        return FindProductQuery.from(repository.save(product));
    }

    // @Cacheable 은 해당 메서드가 실행되기 전에 SpEL 을 평가한다.
    @Cacheable(
            cacheNames = "productRead",
            key = "{ #productId, #role == 'HUB' ? #role : '' }",
            unless = "#result == null" // null 일 경우 캐시하지 않음
    )
    @Transactional(readOnly = true)
    public FindProductQuery read(UUID productId, String username, String role) {
        // DB 를 조회하고 FeignClient 를 호출 해 자격 검증하는 것과
        // FeignClient 를 호출해 자격 조건으로 DB 를 조회하는 것 중 어느 것이 성능이 더 좋을까?

        FindProductQuery findProduct = repository.findByProductIdAndDeletedAtIsNull(productId)
                .map(FindProductQuery::from)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if ("HUB".equals(role)) {
            Ids ids = getCompanyIdAndHubId(username);
            if (!findProduct.hubId().equals(ids.hubId())) {
                throw new CustomException(PRODUCT_UNAUTHORIZED);
            }
        }

        return findProduct;
    }

    @CachePut(
            cacheNames = "productRead",
            key = "{ #command.productId, #role == 'HUB' ? #role : '' }",
            unless = "#result == null"
    )
    @CacheEvict(cacheNames = "productSearch", allEntries = true)
    public void update(UpdateProductCommand command, String username, String role) {
        Product updateProduct = repository.findByProductIdAndDeletedAtIsNull(command.productId())
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (!"MASTER".equals(role)) {
            Ids ids = getCompanyIdAndHubId(username);

            boolean isUnauthorized = switch (role) {
                case "COMPANY" -> !ids.companyId().equals(updateProduct.getCompanyId());
                case "HUB" -> !ids.hubId().equals(updateProduct.getHubId());
                default -> false;
            };

            if (isUnauthorized) {
                throw new CustomException(PRODUCT_UNAUTHORIZED);
            }
        }

        updateProduct.updateOf(
                command.name(),
                command.price(),
                command.stockQuantity()
        );
    }

    @Caching(evict = {
            @CacheEvict(
                    cacheNames = "productRead",
                    key = "{ #productId, #role == 'HUB' ? #role : '' }"
            ),
            @CacheEvict(cacheNames = "productSearch", allEntries = true)
    })
    public void delete(UUID productId, String username, String role) {
        // 해당 상품이 존재하는 지 확인
        Product deleteProduct = repository.findByProductIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        // 허브 담당자라면 상품이 내 허브 관리 대상인지 확인
        if (role.equals("HUB")) {
            UUID userHubId = companyPort.getCompanyByUsername(username).data().hubId();
            if (!userHubId.equals(deleteProduct.getHubId())) {
                throw new CustomException(PRODUCT_UNAUTHORIZED);
            }
        }
        deleteProduct.deleteOf(username);
    }

    @Cacheable(
            cacheNames = "productSearch",
            key = "{ #keyword, #pageable.pageNumber, #pageable.pageSize, #role == 'HUB' ? #role : '' }",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public Page<FindProductQuery> search(
            String keyword,
            Pageable pageable,
            String username,
            String role
    ) {
        // 허브 담당자는 담당 허브의 상품만 조회 가능하다.
        UUID hubId = Optional.ofNullable("HUB".equals(role) ?
                        companyPort.getCompanyByUsername(username) : null
                )
                .map(company -> company.data().hubId())
                .orElse(null);

        return repositoryCustom.retrieve(keyword, pageable, hubId)
                .map(FindProductQuery::from);
    }

    private Ids getCompanyIdAndHubId(String username) {
        // FeignClient 호출 companyPort, hubPort
        FindCompanyQuery company = companyPort.getCompanyByUsername(username);

        UUID companyId = Optional.ofNullable(company)
                .filter(c -> c.code() == 200)
                .map(c -> c.data().id())
                .orElseThrow(() -> new CustomException(COMPANY_NOT_FOUND));

        UUID hubId = Optional.of(company)
                .map(c -> c.data().hubId())
                .orElse(null); // hubId가 없어도 예외 처리 X

        if (hubId != null) {
            FindHubQuery hub = hubPort.getHubByHubId(hubId.toString(), 1, 1);
            if (hub == null || hub.code() != 200) {
                throw new CustomException(HUB_NOT_FOUND);
            }
        }
        return new Ids(companyId, hubId);
    }

    private record Ids(UUID companyId, UUID hubId) {
    }
}
