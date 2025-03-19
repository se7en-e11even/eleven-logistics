package com.eleven.logistics.hubrouteservice.application.service;

import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.hubrouteservice.application.dto.HubResponseDto;
import com.eleven.logistics.hubrouteservice.application.dto.HubRouteResponseDto;
import com.eleven.logistics.hubrouteservice.application.dto.MapDto;
import com.eleven.logistics.hubrouteservice.application.dto.ProcessHubRouteCommand;
import com.eleven.logistics.hubrouteservice.application.service.external.HubService;
import com.eleven.logistics.hubrouteservice.application.service.external.RouteService;
import com.eleven.logistics.hubrouteservice.domain.entity.HubRoute;
import com.eleven.logistics.hubrouteservice.domain.repository.HubRouteRepository;
import com.eleven.logistics.hubrouteservice.domain.service.HubRouteDomainService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class HubRouteService {

    private final HubRouteRepository hubRouteRepository;
    private final RouteService routeService;
    private final HubService hubService;
    private final HubRouteDomainService hubRouteDomainService;

    public HubRouteService(HubRouteRepository hubRouteRepository, RouteService routeService, HubService hubService) {
        this.hubRouteRepository = hubRouteRepository;
        this.routeService = routeService;
        this.hubService = hubService;
        this.hubRouteDomainService = new HubRouteDomainService();
    }

    @Transactional
    public HubRouteResponseDto createHubRoute(ProcessHubRouteCommand hubRouteCommand) {


        // hub-service에서 Hub 정보를 가져오는 부분
        ResponseEntity<ApiResponseDto<HubResponseDto>> originResponse = hubService.getHubById(hubRouteCommand.getOriginHubId());
        ResponseEntity<ApiResponseDto<HubResponseDto>> destinationResponse = hubService.getHubById(hubRouteCommand.getDestinationHubId());

        // ApiResponseDto에서 HubResponseDto를 꺼내기
        HubResponseDto originHub = originResponse.getBody().getData(); // ApiResponseDto에서 data를 꺼냄
        HubResponseDto destinationHub = destinationResponse.getBody().getData(); // ApiResponseDto에서 data를 꺼냄

        // 데이터가 존재하지 않으면 예외 처리
        if (originHub == null || destinationHub == null) {
            throw new RuntimeException("허브 정보를 찾을 수 없습니다.");
        }

        // 출발 -> 도착 경로가 이미 존재하는지 확인 (중복 체크)
        Optional<HubRoute> existingForwardRoute = hubRouteRepository.findByOriginHubIdAndDestinationHubId(hubRouteCommand.getOriginHubId(), hubRouteCommand.getDestinationHubId());
        if (existingForwardRoute.isPresent()) {
            throw new RuntimeException("이미 동일한 경로가 존재합니다.");
        }

        // 도착 -> 출발 경로가 이미 존재하는지 확인 (역방향 경로 중복 체크)
        Optional<HubRoute> existingReverseRoute = hubRouteRepository.findByOriginHubIdAndDestinationHubId(hubRouteCommand.getDestinationHubId(), hubRouteCommand.getOriginHubId());
        if (existingReverseRoute.isPresent()) {
            throw new RuntimeException("역방향 경로가 이미 존재합니다.");
        }

        String originCoords = originHub.getLongitude() + "," + originHub.getLatitude();
        log.info(originCoords);
        String destinationCoords = destinationHub.getLongitude() + "," + destinationHub.getLatitude();
        log.info(destinationCoords);
        // KakaoMapClient를 통해 거리 및 소요 시간 조회
        MapDto kakaoMapDto = routeService.getRoute(originCoords, destinationCoords);

        if (kakaoMapDto == null) {
            throw new RuntimeException("맵 API에서 거리 및 시간 정보를 가져올 수 없습니다.");
        }

        // 출발 -> 도착 경로 생성
        HubRoute forwardRoute = HubRoute.create(
                hubRouteCommand.getOriginHubId(),
                hubRouteCommand.getDestinationHubId(),
                originHub.getName(),
                destinationHub.getName(),
                kakaoMapDto.getDuration(),
                kakaoMapDto.getDistance()
        );
        hubRouteRepository.save(forwardRoute);

        // 도착 -> 출발 경로 생성 (역방향 경로)
        HubRoute reverseRoute = HubRoute.create(
                hubRouteCommand.getDestinationHubId(),
                hubRouteCommand.getOriginHubId(),
                destinationHub.getName(),
                originHub.getName(),
                kakaoMapDto.getDuration(),
                kakaoMapDto.getDistance()
        );
        hubRouteRepository.save(reverseRoute);

        // HubRouteResponseDto에 저장된 데이터 반환
        return HubRouteResponseDto.of(forwardRoute);
    }




    @Cacheable(value = "optimalRoutes", key = "#originHubId + ':' + #destinationHubId")
    @Transactional
    public List<Map<String, UUID>> findOptimalRoute(UUID originHubId, UUID destinationHubId) {


        // hub-service에서 Hub 정보를 가져오는 부분
        ResponseEntity<ApiResponseDto<HubResponseDto>> originResponse = hubService.getHubById(originHubId);
        ResponseEntity<ApiResponseDto<HubResponseDto>> destinationResponse = hubService.getHubById(destinationHubId);

        // ApiResponseDto에서 HubResponseDto를 꺼내기
        HubResponseDto originHub = originResponse.getBody().getData(); // ApiResponseDto에서 data를 꺼냄
        HubResponseDto destinationHub = destinationResponse.getBody().getData(); // ApiResponseDto에서 data를 꺼냄

        // 데이터가 존재하지 않으면 예외 처리
        if (originHub == null || destinationHub == null) {
            throw new RuntimeException("허브 정보를 찾을 수 없습니다.");
        }

        // 최적 경로 계산
        List<Map<String, UUID>> optimalRoute = hubRouteDomainService.findOptimalRoute(hubRouteRepository.findAll(), originHubId, destinationHubId);

        return optimalRoute;
    }
}
