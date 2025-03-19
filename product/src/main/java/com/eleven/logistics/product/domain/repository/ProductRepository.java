package com.eleven.logistics.product.domain.repository;

import com.eleven.logistics.product.domain.entity.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findByProductIdAndDeletedAtIsNull(UUID productId);
}
