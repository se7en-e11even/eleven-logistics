package com.eleven.logistics.slack.common.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Slf4j(topic = "Order Service JpaAuditor")
@Component
public class JpaAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            log.info("RequestAttributes is null");

            // 필요한 정보가 없을 경우 Optional.empty() 를 반환하여 예외 방지.
            return Optional.empty();
        }

        HttpServletRequest request = attributes.getRequest();
        String username = request.getHeader("X-Username");
        log.info("X-Username: {}", username);
        return Optional.ofNullable(username);
    }
}
