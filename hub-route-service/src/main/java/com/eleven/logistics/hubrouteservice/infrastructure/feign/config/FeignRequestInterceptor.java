package com.eleven.logistics.hubrouteservice.infrastructure.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    private final HttpServletRequest request;

    public FeignRequestInterceptor(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void apply(RequestTemplate template) {
        // 클라이언트 요청에서 'X-Role' 헤더를 가져와서 Feign 요청 헤더에 추가
        String role = request.getHeader("X-Role");
        if (role != null && !role.isEmpty()) {
            template.header("X-Role", role); // Feign 요청에 X-Role 헤더 추가
        }
    }
}