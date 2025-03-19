package com.eleven.logistics.hub.infrastructure.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 클라이언트 요청에서 'X-Role' 헤더를 가져와서 Feign 요청 헤더에 추가
        template.header("X-Role", "MASTER");
    }
}
