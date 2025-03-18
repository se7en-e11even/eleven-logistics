package com.eleven.logistics.hub.domain.repository.hub;

import com.eleven.logistics.hub.domain.entity.hub.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface HubRepository {
    Hub save(Hub hub);

    Optional<Hub> findById(UUID hubId);

    Page<Hub> findByDeletedAtIsNull(Pageable pageable);

    boolean existsByAddress(String address);
}
