package com.eleven.logistics.hubrouteservice.infrastructure.repository;

import com.eleven.logistics.hubrouteservice.domain.entity.HubRoute;
import com.eleven.logistics.hubrouteservice.domain.repository.HubRouteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class HubRouteRepositoryImpl implements HubRouteRepository {
    private final HubRouteJpaRepository hubRouteJpaRepository;

    public HubRouteRepositoryImpl(HubRouteJpaRepository hubRouteJpaRepository) {
        this.hubRouteJpaRepository = hubRouteJpaRepository;
    }

    @Override
    public Optional<HubRoute> findByOriginHubIdAndDestinationHubId(UUID originHubId, UUID destinationHubId) {
        return hubRouteJpaRepository.findByOriginHubIdAndDestinationHubId(originHubId, destinationHubId);
    }

    @Override
    public HubRoute save(HubRoute hubRoute) {
        return hubRouteJpaRepository.save(hubRoute);
    }
    @Override
    public List<HubRoute> findAll() {
        return hubRouteJpaRepository.findAll();
    }
}
