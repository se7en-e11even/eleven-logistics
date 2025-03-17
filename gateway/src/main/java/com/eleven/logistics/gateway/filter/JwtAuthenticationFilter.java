package com.eleven.logistics.gateway.filter;

import com.eleven.logistics.gateway.exception.UnAuthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    public static final int AUTHENTICATION_FILTER_ORDER = 100;
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_ROLE = "X-Role";
    private static final String JWT_USERNAME = "username";
    private static final String JWT_ROLE = "role";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.equals("/auth/signIn") || path.equals("/auth/signUp") || path.equals("/auth/signOut")) {
            return chain.filter(exchange);  // 위의 경로는 필터를 적용하지 않음
        }

        String token = extractToken(exchange);
        Claims payload = parsePayload(token);
        validateExpirationDate(payload.getExpiration());

        String username = payload.get(JWT_USERNAME, String.class);
        String role = payload.get(JWT_ROLE, String.class);

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(HEADER_USERNAME, String.valueOf(username))
                .header(HEADER_ROLE, String.valueOf(role))
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange);
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null && !authHeader.startsWith("Bearer ")) {
            throw new UnAuthorizedException("유효하지 않은 토큰 정보");
        }
        return authHeader.substring(7);
    }

    private Claims parsePayload(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
            return Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();
        } catch (JwtException exception) {
            throw new UnAuthorizedException("유효하지 않은 토큰 정보");
        }
    }

    private void validateExpirationDate(Date expirationDate) {
        Date currentDate = new Date(System.currentTimeMillis());
        if (currentDate.after(expirationDate)) {
            throw new UnAuthorizedException("만료된 토큰");
        }
    }

    @Override
    public int getOrder() {
        return AUTHENTICATION_FILTER_ORDER;
    }
}