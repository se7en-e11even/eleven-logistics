package com.eleven.logistics.product.domain.repository;

import com.eleven.logistics.product.application.dto.command.ListProductCommand;
import com.eleven.logistics.product.application.dto.query.FindProductQuery;
import com.eleven.logistics.product.application.dto.query.ListProductQuery;

public interface ProductRepositoryCustom {

    // queryDSL 에 적용할 메서드
    ListProductQuery<FindProductQuery> retrieve(String keyword, ListProductCommand command);
}
