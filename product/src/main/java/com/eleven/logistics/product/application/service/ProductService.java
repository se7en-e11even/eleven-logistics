package com.eleven.logistics.product.application.service;

import com.eleven.logistics.product.application.dto.command.CreateProductCommand;
import com.eleven.logistics.product.application.dto.command.UpdateProductCommand;
import com.eleven.logistics.product.application.dto.query.FindCompanyQuery;
import com.eleven.logistics.product.application.dto.query.FindHubQuery;
import com.eleven.logistics.product.application.dto.query.FindProductQuery;
import com.eleven.logistics.product.application.port.out.CompanyPort;
import com.eleven.logistics.product.application.port.out.HubPort;
import com.eleven.logistics.product.domain.exception.CustomException;
import com.eleven.logistics.product.domain.entity.Product;
import com.eleven.logistics.product.domain.repository.ProductRepository;
import com.eleven.logistics.product.domain.repository.ProductRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.eleven.logistics.product.domain.exception.ProductErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductRepositoryCustom repositoryCustom;
    private final CompanyPort companyPort;
    private final HubPort hubPort;

    @Transactional
    public UUID create(CreateProductCommand command, String username) {
        // 생성 유저가 속한 상품 업체가 존재하는 지 확인(상품 업체로 부터 관리 허브 id도 받아옴)
        Company company = getCompany(username);

        // 엔티티에 객체 생성에 대한 책임을 부여한다.
        Product product = Product.builder()
                .companyId(company.companyId())
                .hubId(company.hubId())
                .name(command.name())
                .price(command.price())
                .stockQuantity(command.quantity())
                .build();

        repository.save(product);

        // Dirty Checking 으로 productId 가 넣어진다.
        return product.getProductId();
    }

    public FindProductQuery read(UUID productId) {
        return repository.findByProductIdAndDeletedAtIsNull(productId)
                .map(FindProductQuery::of)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }

    @Transactional
    public void update(UpdateProductCommand command) {
        Product updateProduct = repository.findByProductIdAndDeletedAtIsNull(command.productId())
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        updateProduct.updateOf(
                command.name(),
                command.price(),
                command.stockQuantity()
        );
    }

    @Transactional
    public void delete(UUID productId, String username) {
        // 해당 상품이 존재하는 지 확인
        Product deleteProduct = repository.findByProductIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
        deleteProduct.deleteOf(username);
    }

    @Transactional(readOnly = true)
    public Page<FindProductQuery> search(
            String keyword,
            Pageable pageable
    ) {
        return repositoryCustom.retrieve(keyword, pageable)
                .map(FindProductQuery::of);
    }

    private Company getCompany(String username) {
        // FeignClient 호출 companyPort, hubPort

        FindCompanyQuery company = companyPort.getCompanyByUsername(username);
        // 정상 응답인 지 확인
        if (company == null || company.code() != 200) {
            throw new CustomException(COMPANY_NOT_FOUND);
        }
        UUID companyId = company.data().id();

        // hubId가 존재하는 지도 검증해야 한다!
        UUID hubId = company.data().hubId();
        FindHubQuery hub = hubPort.getHubByHubId(hubId.toString());
        if (hub == null || hub.code() != 200) {
            throw new CustomException(HUB_NOT_FOUND);
        }
        return new Company(companyId, hubId);
    }

    private record Company(UUID companyId, UUID hubId) {
    }
}
