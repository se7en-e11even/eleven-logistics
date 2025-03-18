package com.eleven.logistics.hubrouteservice.application.service.external;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OptimalRouteCacheService {
    void saveOptimalRoute(UUID originHubId, UUID destinationHubId, List<Map<String, UUID>> route);

    List<Map<String, UUID>> getOptimalRoute(UUID originHubId, UUID destinationHubId);


}
