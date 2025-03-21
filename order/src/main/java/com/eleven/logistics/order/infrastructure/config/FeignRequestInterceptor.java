package com.eleven.logistics.order.infrastructure.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j(topic = "Order Service FeignRequestInterceptor")
@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 현재 요청의 RequestAttributes 를 가져옴
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();

            // 클라이언트 요청에서 username 과 role 헤더를 가져와 Feign 요청 헤더에 추가
            String username = request.getHeader("X-Username");
            String role = request.getHeader("X-Role");

            if (username != null) {
                requestTemplate.header("X-Username", username);
            }
            if (role != null) {
                requestTemplate.header("X-Role", role);
            }
        }
    }
}
