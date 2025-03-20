package com.eleven.logistics.product.infrastructure.repository;

import com.eleven.logistics.product.domain.entity.Product;
import com.eleven.logistics.product.domain.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<Product, UUID>, ProductRepository {
}
