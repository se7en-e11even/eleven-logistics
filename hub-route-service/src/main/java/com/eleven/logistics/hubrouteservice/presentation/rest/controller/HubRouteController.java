package com.eleven.logistics.hubrouteservice.presentation.rest.controller;

import com.eleven.logistics.hubrouteservice.application.dto.HubRouteResponseDto;
import com.eleven.logistics.hubrouteservice.application.service.HubRouteService;
import com.eleven.logistics.hubrouteservice.presentation.rest.dto.HubRouteRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/hub-routes")
public class HubRouteController {
    private final HubRouteService hubRouteService;

    public HubRouteController(HubRouteService hubRouteService) {
        this.hubRouteService = hubRouteService;
    }

    @PostMapping
    public ResponseEntity<HubRouteResponseDto> createHubRoute(@RequestBody HubRouteRequestDto requestDto) {
        return ResponseEntity.ok(hubRouteService.createHubRoute(requestDto.toCommand()));
    }

    // 출발 허브 → 도착 허브까지 최적 경로 찾기
    @GetMapping
    public ResponseEntity<List<Map<String, UUID>>> findOptimalRoute(@RequestBody HubRouteRequestDto requestDto) {
        return ResponseEntity.ok(hubRouteService.findOptimalRoute(requestDto.toCommand()));
    }

}
