package com.eleven.logistics.product.application.port.out;

import com.eleven.logistics.product.application.dto.query.FindCompanyQuery;

/**
 * Feign Client 호출은 외부 사항이다. 실제 구현체는 infra 영역에 존재 하도록 하여
 * 계층 간 의존 방향이 일치 되도록 한다.
 * ProductService 가 Feign Client 에 직접 의존하지 않도록
 * 의존성 역전을 사용하기 위해 외부 api 를 호출하는 인터페이스를 작성한다.
 */
public interface CompanyPort {
    FindCompanyQuery getCompanyByUsername(String username);
}
