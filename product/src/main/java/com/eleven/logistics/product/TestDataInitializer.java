package com.eleven.logistics.product;

import com.eleven.logistics.product.domain.entity.Product;
import com.eleven.logistics.product.infrastructure.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final ProductJpaRepository repository;

    @Override
    public void run(String... args) throws Exception {
        Product notebook = Product.builder()
                .companyId(UUID.randomUUID())
                .hubId(UUID.randomUUID())
                .name("notebook")
                .price(1000000)
                .stockQuantity(5)
                .build();
        Product macbook = Product.builder()
                .companyId(UUID.randomUUID())
                .hubId(UUID.randomUUID())
                .name("macbook")
                .price(1500000)
                .stockQuantity(10)
                .build();
        Product ipad = Product.builder()
                .companyId(UUID.randomUUID())
                .hubId(UUID.randomUUID())
                .name("ipad")
                .price(800000)
                .stockQuantity(20)
                .build();
        List<Product> products = List.of(notebook, macbook, ipad);
        repository.saveAll(products);
    }
}
