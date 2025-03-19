package com.eleven.logistics.hubrouteservice.domain.repository;

import com.eleven.logistics.hubrouteservice.domain.entity.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Interface 계층에 존재하는 JpaRepository 를 분리하기 위한 Interface
@Repository
public interface HubRouteRepository {
    Optional<HubRoute> findByOriginHubIdAndDestinationHubId(UUID originHubId, UUID destinationHubId);
    HubRoute save(HubRoute hubRoute);
    List<HubRoute> findAll();
}
