package com.eleven.logistics.hubrouteservice.domain.service;

import com.eleven.logistics.hubrouteservice.domain.entity.HubRoute;
import com.eleven.logistics.hubrouteservice.domain.exception.HubRouteNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class HubRouteDomainService {

    public List<Map<UUID, UUID>> findOptimalRoute(List<HubRoute> routes, UUID originHubId, UUID destinationHubId) {

        // Graph 모델링: 출발 허브 ID → [인접한 도착 허브 목록]
        Map<UUID, List<HubRoute>> graph = new HashMap<>();
        for (HubRoute route : routes) {
            graph.computeIfAbsent(route.getOriginHubId(), k -> new ArrayList<>()).add(route);
        }

        // 다익스트라 알고리즘 초기화
        Map<UUID, Integer> distances = new HashMap<>();
        Map<UUID, UUID> previous = new HashMap<>();
        PriorityQueue<UUID> queue = new PriorityQueue<>(Comparator.comparing(distances::get));

        for (HubRoute route : routes) {
            distances.put(route.getOriginHubId(), Integer.MAX_VALUE);
            distances.put(route.getDestinationHubId(), Integer.MAX_VALUE);
        }

        // 출발 허브의 거리를 0으로 설정 후 탐색 시작
        distances.put(originHubId, 0);
        queue.add(originHubId);

        while (!queue.isEmpty()) {
            UUID currentHub = queue.poll();

            // 현재 허브에서 이동 가능한 인접 허브 탐색
            for (HubRoute neighbor : graph.getOrDefault(currentHub, new ArrayList<>())) {
                UUID nextHub = neighbor.getDestinationHubId();
                int newDistance = distances.get(currentHub) + neighbor.getDuration();

                if (newDistance < distances.get(nextHub)) {
                    distances.put(nextHub, newDistance);
                    previous.put(nextHub, currentHub);
                    queue.add(nextHub);
                }
            }
        }


        // 최적 경로 추적 (출발 → 도착 형식으로 저장)
        List<Map<UUID, UUID>> path = new ArrayList<>();
        UUID current = destinationHubId;

        while (previous.containsKey(current)) {
            UUID prevHub = previous.get(current);
            Map<UUID, UUID> routeMap = new HashMap<>();
            routeMap.put(prevHub, current); // 출발 허브와 도착 허브를 UUID 타입으로 저장
            path.add(routeMap);
            current = prevHub;
        }

        Collections.reverse(path); // 경로를 올바른 순서로 정렬

        // 예외 처리: 경로가 없거나, 첫 출발 허브가 요청한 originHubId와 다르면 예외 발생
        if (path.isEmpty() || !path.get(0).keySet().iterator().next().equals(originHubId)) {
            throw new HubRouteNotFoundException("출발 허브 ID " + originHubId + "에서 도착 허브 ID " + destinationHubId + "까지의 경로를 찾을 수 없습니다.");
        }

        return path.isEmpty() || !path.get(0).keySet().iterator().next().equals(originHubId) ? Collections.emptyList() : path;

    }
}
