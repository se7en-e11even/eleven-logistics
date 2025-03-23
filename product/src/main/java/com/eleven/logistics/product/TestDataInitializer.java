package com.eleven.logistics.product;

import com.eleven.logistics.product.domain.entity.Product;
import com.eleven.logistics.product.infrastructure.repository.ProductJpaRepository;
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
        if (repository.count() == 0) {
            Product notebook = Product.builder()
                    .companyId(UUID.fromString("c3cb43e1-b373-4687-aa9d-4ad8183bb3ea"))
                    .hubId(UUID.fromString("0f07d344-e0a3-4a82-9bbd-369632bb037f"))
                    .name("notebook")
                    .price(1000000)
                    .stockQuantity(5)
                    .build();
            Product macbook = Product.builder()
                    .companyId(UUID.fromString("9b8589a6-dcf7-4f63-8eae-32114cb48937"))
                    .hubId(UUID.fromString("a609c352-0796-4e59-8097-636c39f48086"))
                    .name("macbook")
                    .price(1500000)
                    .stockQuantity(10)
                    .build();
            Product ipad = Product.builder()
                    .companyId(UUID.fromString("3f8615bc-6035-4363-b4cd-81db0b3c7317"))
                    .hubId(UUID.fromString("5a2e725b-e0c7-4a43-9dea-009499997dfb"))
                    .name("ipad")
                    .price(800000)
                    .stockQuantity(20)
                    .build();
            List<Product> products = List.of(notebook, macbook, ipad);
            repository.saveAll(products);
        }
    }
}
