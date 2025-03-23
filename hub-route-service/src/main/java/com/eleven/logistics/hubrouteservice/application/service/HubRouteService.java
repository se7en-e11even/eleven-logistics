package com.eleven.logistics.hubrouteservice.application.service;

import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.hubrouteservice.application.dto.HubResponseDto;
import com.eleven.logistics.hubrouteservice.application.dto.HubRouteResponseDto;
import com.eleven.logistics.hubrouteservice.application.dto.MapDto;
import com.eleven.logistics.hubrouteservice.application.dto.ProcessHubRouteCommand;
import com.eleven.logistics.hubrouteservice.application.dto.feign.CreateDeliveryRouteRequest;
import com.eleven.logistics.hubrouteservice.application.dto.feign.DeliveryRouteRequest;
import com.eleven.logistics.hubrouteservice.application.dto.feign.DeliveryRouteResponse;
import com.eleven.logistics.hubrouteservice.application.service.external.DeliveryService;
import com.eleven.logistics.hubrouteservice.application.service.external.HubService;
import com.eleven.logistics.hubrouteservice.application.service.external.RouteService;
import com.eleven.logistics.hubrouteservice.domain.entity.HubRoute;
import com.eleven.logistics.hubrouteservice.domain.repository.HubRouteRepository;
import com.eleven.logistics.hubrouteservice.domain.service.HubRouteDomainService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class HubRouteService {

    private final HubRouteRepository hubRouteRepository;
    private final RouteService routeService;
    private final HubService hubService;
    private final HubRouteDomainService hubRouteDomainService;
    private final DeliveryService deliveryService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String SEQUENCE_KEY = "delivery_sequence"; // Redis 키

    public HubRouteService(HubRouteRepository hubRouteRepository, RouteService routeService, HubService hubService, DeliveryService deliveryService, RedisTemplate<String, String> redisTemplate) {
        this.hubRouteRepository = hubRouteRepository;
        this.routeService = routeService;
        this.hubService = hubService;
        this.hubRouteDomainService = new HubRouteDomainService();
        this.deliveryService = deliveryService;
        this.redisTemplate = redisTemplate;
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

    public void createDeliveryRoutes(List<Map<String, UUID>> routes, UUID deliveryId){
        int sequence = getNextSequence() + 1; // Redis에서 sequence 값 가져오기

        List<CreateDeliveryRouteRequest> requestList = new ArrayList<>();

        // deliveryRoute 생성
        for (Map<String, UUID> route : routes) { // 허브를 거쳐가는 수 만큼 반복
            String originHubId = route.keySet().iterator().next(); // 출발 허브 ID
            UUID destinationHubId = route.get(originHubId); // 도착 허브 ID (경유 허브)

            HubRoute hubRoute = hubRouteRepository.findByOriginHubIdAndDestinationHubId(UUID.fromString(originHubId), destinationHubId)
                    .orElseThrow(() -> new IllegalArgumentException("HubRoute not found for the given originHubId and destinationHubId"));

            int duration = hubRoute.getDuration();
            int distance = hubRoute.getDistance();

            // CreateDeliveryRouteRequest 생성
            CreateDeliveryRouteRequest request = new CreateDeliveryRouteRequest(
                    deliveryId,               // deliveryId
                    getNextSequence(),               // sequence (순차적으로 증가)
                    UUID.fromString(originHubId),             // departureHubId
                    destinationHubId,        // arrivalHubId
                    distance,                // expectedDistance
                    duration                 // expectedTime
            );
            updateSequence(getNextSequence());

            requestList.add(request);
        }

        List<UUID> routeIdList = deliveryService.createDeliveryRoute(deliveryId, requestList);
        int i=0;
        // 실제시간 계산
        for (Map<String, UUID> route : routes) { // 허브를 거쳐가는 수 만큼 반복

            String originHubId = route.keySet().iterator().next(); // 출발 허브 ID

            UUID destinationHubId = route.get(originHubId); // 도착 허브 ID (경유 허브)


            HubRoute hubRoute = hubRouteRepository.findByOriginHubIdAndDestinationHubId(UUID.fromString(originHubId), destinationHubId)
                    .orElseThrow(() -> new IllegalArgumentException("HubRoute not found for the given originHubId and destinationHubId"));


            MapDto kakaoMapDto = getActualDistanceAndDuration(UUID.fromString(originHubId), destinationHubId);
            int duration = kakaoMapDto.getDuration();
            int distance = kakaoMapDto.getDistance();


            DeliveryRouteRequest updateRequest = new DeliveryRouteRequest(
                    UUID.fromString(originHubId),
                    destinationHubId,
                    distance,
                    duration,
                    "허브 도착"
            );

            deliveryService.updateDeliveryRoute(deliveryId, routeIdList.get(i), updateRequest);
            i++;
        }

    }

    // Redis에서 다음 sequence 번호를 가져오는 메서드
    private int getNextSequence() {
        String lastSequence = redisTemplate.opsForValue().get(SEQUENCE_KEY);
        int nextSequence = (lastSequence != null ? Integer.parseInt(lastSequence) : 0) % 10 + 1;
        return nextSequence;
    }

    // Redis에 sequence 번호 업데이트
    private void updateSequence(int sequence) {
        redisTemplate.opsForValue().set(SEQUENCE_KEY, String.valueOf(sequence));
    }

    private MapDto getActualDistanceAndDuration(UUID originHubId, UUID destinationHubId) {
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

        String originCoords = originHub.getLongitude() + "," + originHub.getLatitude();
        String destinationCoords = destinationHub.getLongitude() + "," + destinationHub.getLatitude();
        // KakaoMapClient를 통해 거리 및 소요 시간 조회
        MapDto kakaoMapDto = routeService.getRoute(originCoords, destinationCoords);

        if (kakaoMapDto == null) {
            throw new RuntimeException("맵 API에서 거리 및 시간 정보를 가져올 수 없습니다.");
        }

        return kakaoMapDto;
    }
}
