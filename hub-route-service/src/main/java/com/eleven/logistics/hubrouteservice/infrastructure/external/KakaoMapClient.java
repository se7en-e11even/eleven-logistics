package com.eleven.logistics.hubrouteservice.infrastructure.external;

import com.eleven.logistics.hubrouteservice.application.dto.MapDto;
import com.eleven.logistics.hubrouteservice.application.service.external.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class KakaoMapClient implements RouteService {
    private static final String KAKAO_API_URL = "https://apis-navi.kakaomobility.com/v1/directions";
    private static final String API_KEY = "KakaoAK a7c04319b95dd26cf09d9af2b55a1fe3";

    private final RestTemplate restTemplate;

    public KakaoMapClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MapDto getRoute(String origin, String destination) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = KAKAO_API_URL + "?origin=" + origin + "&destination=" + destination;

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            return extractDistanceAndDuration(responseBody);
        }

        throw new RuntimeException("Failed to fetch route information from Kakao API");
    }

    private MapDto extractDistanceAndDuration(Map<String, Object> responseBody) {
        try {
            Map<String, Object> route = ((Map<String, Object>) ((java.util.List<?>) responseBody.get("routes")).get(0));
            Map<String, Object> summary = (Map<String, Object>) route.get("summary");

            int distance = (int) summary.get("distance"); // 미터
            int duration = (int) summary.get("duration"); // 초

            return new MapDto(distance, duration);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Kakao API response", e);
        }
    }
}
