package com.eleven.logistics.hubrouteservice.application.service;

import com.eleven.logistics.hubrouteservice.application.dto.HubResponseDto;
import com.eleven.logistics.hubrouteservice.application.dto.HubRouteResponseDto;
import com.eleven.logistics.hubrouteservice.application.dto.MapDto;
import com.eleven.logistics.hubrouteservice.application.dto.ProcessHubRouteCommand;
import com.eleven.logistics.hubrouteservice.application.service.external.HubService;
import com.eleven.logistics.hubrouteservice.application.service.external.OptimalRouteCacheService;
import com.eleven.logistics.hubrouteservice.application.service.external.RouteService;
import com.eleven.logistics.hubrouteservice.domain.entity.HubRoute;
import com.eleven.logistics.hubrouteservice.domain.repository.HubRouteRepository;
import com.eleven.logistics.hubrouteservice.domain.service.HubRouteDomainService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class HubRouteService {

    private final HubRouteRepository hubRouteRepository;
    private final RouteService routeService;
    private final HubService hubService;
    private final HubRouteDomainService hubRouteDomainService;
    private final OptimalRouteCacheService optimalRouteCacheService;

    public HubRouteService(HubRouteRepository hubRouteRepository, RouteService routeService, HubService hubService, OptimalRouteCacheService optimalRouteCacheService) {
        this.hubRouteRepository = hubRouteRepository;
        this.routeService = routeService;
        this.hubService = hubService;
        this.hubRouteDomainService = new HubRouteDomainService();
        this.optimalRouteCacheService = optimalRouteCacheService;
    }

    @Transactional
    public HubRouteResponseDto createHubRoute(ProcessHubRouteCommand hubRouteCommand) {

        // hub-service와 feign-client를 이용해 origin과 destination의 위도, 경도를 조회
        HubResponseDto originHub = hubService.getHubById(hubRouteCommand.getOriginHubId());
        HubResponseDto destinationHub = hubService.getHubById(hubRouteCommand.getDestinationHubId());

        if (originHub == null || destinationHub == null) {
            throw new RuntimeException("허브 정보를 찾을 수 없습니다.");
        }

        String originCoords = originHub.getLongitude() + "," + originHub.getLatitude();
        String destinationCoords = destinationHub.getLongitude() + "," + destinationHub.getLatitude();

        // KakaoMapClient를 통해 거리 및 소요 시간 조회
        MapDto kakaoMapDto = routeService.getRoute(originCoords, destinationCoords);

        if (kakaoMapDto == null) {
            throw new RuntimeException("맵 API에서 거리 및 시간 정보를 가져올 수 없습니다.");
        }

        // HubRoute 엔티티 저장
        HubRoute hubRoute = HubRoute.create(
                hubRouteCommand.getOriginHubId(),
                hubRouteCommand.getDestinationHubId(),
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
    public List<Map<String, UUID>> findOptimalRoute(ProcessHubRouteCommand hubRouteCommand) {

        // request에서 originHubId, destinationHubId 추출
        UUID originHubId = hubRouteCommand.getOriginHubId();
        UUID destinationHubId = hubRouteCommand.getDestinationHubId();

        // Redis에서 최적 경로가 있는지 확인 (캐싱된 데이터가 있으면 반환)
        List<Map<String, UUID>> cachedRoute = optimalRouteCacheService.getOptimalRoute(originHubId, destinationHubId);
        if (cachedRoute != null) {
            return cachedRoute;
        }

        // 출발 및 도착 허브 정보 조회
        HubResponseDto originHub = hubService.getHubById(originHubId);
        HubResponseDto destinationHub = hubService.getHubById(destinationHubId);

        if (originHub == null || destinationHub == null) {
            throw new RuntimeException("허브 정보를 찾을 수 없습니다.");
        }

        // HubRoute 테이블에서 출발-도착 허브 간 경로가 있는지 확인
        Optional<HubRoute> existingRoute = hubRouteRepository.findByOriginHubIdAndDestinationHubId(originHubId, destinationHubId);

        // 경로가 없으면 `createHubRoute` 실행 후 다시 `findOptimalRoute` 실행
        if (existingRoute.isEmpty()) {
            createHubRoute(hubRouteCommand);
        }

        // 최적 경로 계산
        List<Map<String, UUID>> optimalRoute = hubRouteDomainService.findOptimalRoute(hubRouteRepository.findAll(), originHubId, destinationHubId);

        // Redis에 최적 경로 캐싱
        optimalRouteCacheService.saveOptimalRoute(originHubId, destinationHubId, optimalRoute);

        return optimalRoute;
    }
}
