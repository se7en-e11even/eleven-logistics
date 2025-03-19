package com.eleven.logistics.hub.infrastructure.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GeocodingService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.geocode-url}")
    private String geocodeUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeocodingService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
        this.objectMapper = new ObjectMapper();
    }

    // 네이버 API로 주소를 좌표로 변환하는 메서드
    public double[] getCoordinates(String address) {
        try {
            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
            headers.set("X-NCP-APIGW-API-KEY", clientSecret);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestUrl = geocodeUrl + "?query=" + address;

            // 요청 보내기
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            String responseBody = response.getBody();

            // 응답 데이터가 있는지 테스트로 직접 확인
            JsonNode root = objectMapper.readTree(responseBody);
            if (root.has("addresses") && root.path("addresses").size() > 0) {
                double latitude = root.path("addresses").get(0).path("y").asDouble();
                double longitude = root.path("addresses").get(0).path("x").asDouble();
                return new double[]{latitude, longitude};
            } else if (root.has("status") && "OK".equals(root.path("status").asText())) {
                throw new IllegalArgumentException("실제 주소 정보가 없습니다.");
            }
            return new double[]{0.0, 0.0};

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}