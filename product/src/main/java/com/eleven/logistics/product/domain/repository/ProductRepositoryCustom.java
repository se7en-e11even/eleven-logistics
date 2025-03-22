package com.eleven.logistics.product.domain.repository;

import com.eleven.logistics.product.domain.vo.FindProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductRepositoryCustom {

    // queryDSL 에 적용할 메서드
    Page<FindProduct> retrieve(String keyword, Pageable pageable, UUID hubId);
}
