package com.eleven.logistics.hubrouteservice.infrastructure.repository;

import com.eleven.logistics.hubrouteservice.domain.entity.HubRoute;
import com.eleven.logistics.hubrouteservice.domain.repository.HubRouteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HubRouteJpaRepository extends JpaRepository<HubRoute, UUID> {
    Optional<HubRoute> findByOriginHubIdAndDestinationHubId(UUID originHubId, UUID destinationHubId);
}
