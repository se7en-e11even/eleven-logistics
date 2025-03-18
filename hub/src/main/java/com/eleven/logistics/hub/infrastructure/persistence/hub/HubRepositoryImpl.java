package com.eleven.logistics.hub.infrastructure.persistence.hub;

import com.eleven.logistics.hub.domain.entity.hub.Hub;
import com.eleven.logistics.hub.domain.repository.hub.HubRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HubRepositoryImpl extends JpaRepository<Hub, UUID>, HubRepository {
}
