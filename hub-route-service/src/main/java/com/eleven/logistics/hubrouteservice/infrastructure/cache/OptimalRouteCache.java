package com.eleven.logistics.hubrouteservice.infrastructure.cache;

import com.eleven.logistics.hubrouteservice.application.service.external.OptimalRouteCacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class OptimalRouteCache implements OptimalRouteCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long CACHE_EXPIRATION = 24; // 캐시 만료 시간 (24시간)

    public OptimalRouteCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOptimalRoute(UUID originHubId, UUID destinationHubId, List<Map<String, UUID>> route) {
        String key = generateKey(originHubId, destinationHubId);
        redisTemplate.opsForValue().set(key, route, CACHE_EXPIRATION, TimeUnit.HOURS);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, UUID>> getOptimalRoute(UUID originHubId, UUID destinationHubId) {
        String key = generateKey(originHubId, destinationHubId);
        return (List<Map<String, UUID>>) redisTemplate.opsForValue().get(key);
    }

    private String generateKey(UUID originHubId, UUID destinationHubId) {
        return "route:" + originHubId + ":" + destinationHubId;
    }
}
