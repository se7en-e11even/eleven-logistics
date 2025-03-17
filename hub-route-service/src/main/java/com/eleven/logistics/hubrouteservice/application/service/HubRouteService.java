package com.eleven.logistics.hubrouteservice.application.service;

import com.eleven.logistics.hubrouteservice.application.dto.HubResponseDto;
import com.eleven.logistics.hubrouteservice.application.dto.HubRouteResponseDto;
import com.eleven.logistics.hubrouteservice.application.dto.KakaoMapDto;
import com.eleven.logistics.hubrouteservice.domain.entity.HubRoute;
import com.eleven.logistics.hubrouteservice.domain.repository.HubRouteRepository;
import com.eleven.logistics.hubrouteservice.domain.service.HubRouteDomainService;
import com.eleven.logistics.hubrouteservice.infrastructure.external.KakaoMapClient;
import com.eleven.logistics.hubrouteservice.infrastructure.feign.HubServiceClient;
import com.eleven.logistics.hubrouteservice.presentation.rest.dto.HubRouteRequestDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class HubRouteService {

    private final HubRouteRepository hubRouteRepository;
    private final KakaoMapClient kakaoMapClient;
    private final HubServiceClient hubServiceClient;
    private final HubRouteDomainService hubRouteDomainService;

    public HubRouteService(HubRouteRepository hubRouteRepository, KakaoMapClient kakaoMapClient, HubServiceClient hubServiceClient, HubRouteDomainService hubRouteDomainService) {
        this.hubRouteRepository = hubRouteRepository;
        this.kakaoMapClient = kakaoMapClient;
        this.hubServiceClient = hubServiceClient;
        this.hubRouteDomainService = hubRouteDomainService;
    }

    @Transactional
    public HubRouteResponseDto createHubRoute(HubRouteRequestDto requestDto) {

        // hub-service와 feign-client를 이용해 origin과 destination의 위도, 경도를 조회
        HubResponseDto originHub = hubServiceClient.getHubById(requestDto.getOriginHubId());
        HubResponseDto destinationHub = hubServiceClient.getHubById(requestDto.getDestinationHubId());

        if (originHub == null || destinationHub == null) {
            throw new RuntimeException("허브 정보를 찾을 수 없습니다.");
        }

        String originCoords = originHub.getLongitude() + "," + originHub.getLatitude();
        String destinationCoords = destinationHub.getLongitude() + "," + destinationHub.getLatitude();

        // KakaoMapClient를 통해 거리 및 소요 시간 조회
        KakaoMapDto kakaoMapDto = kakaoMapClient.getRoute(originCoords, destinationCoords);

        if (kakaoMapDto == null) {
            throw new RuntimeException("카카오 맵 API에서 거리 및 시간 정보를 가져올 수 없습니다.");
        }

        // HubRoute 엔티티 저장
        HubRoute hubRoute = HubRoute.create(
                requestDto.getOriginHubId(),
                requestDto.getDestinationHubId(),
                originHub.getName(),
                destinationHub.getName(),
                kakaoMapDto.getDuration(),
                kakaoMapDto.getDistance()
        );

        hubRouteRepository.save(hubRoute);

        // HubRouteResponseDto에 저장된 데이터 반환
        return HubRouteResponseDto.of(hubRoute);
    }

    @Transactional
    public List<Map<String, UUID>> findOptimalRoute(HubRouteRequestDto requestDto) {
        UUID originHubId = requestDto.getOriginHubId();
        UUID destinationHubId = requestDto.getDestinationHubId();

        // 출발 및 도착 허브 정보 조회
        HubResponseDto originHub = hubServiceClient.getHubById(originHubId);
        HubResponseDto destinationHub = hubServiceClient.getHubById(destinationHubId);

        if (originHub == null || destinationHub == null) {
            throw new RuntimeException("허브 정보를 찾을 수 없습니다.");
        }

        // HubRoute 테이블에서 출발-도착 허브 간 경로가 있는지 확인
        Optional<HubRoute> existingRoute = hubRouteRepository.findByOriginHubIdAndDestinationHubId(originHubId, destinationHubId);

        // 경로가 없으면 `createHubRoute` 실행 후 다시 `findOptimalRoute` 실행
        if (existingRoute.isEmpty()) {
            createHubRoute(requestDto);
        }

        return hubRouteDomainService.findOptimalRoute(originHubId, destinationHubId);
    }
}
