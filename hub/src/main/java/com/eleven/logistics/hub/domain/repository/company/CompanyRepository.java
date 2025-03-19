package com.eleven.logistics.hub.domain.repository.company;

import com.eleven.logistics.hub.domain.entity.company.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {
    Company save(Company company);

    Optional<Company> findById(UUID companyId);

    boolean existsByAddress(String address);

    Page<Company> findByDeletedAtIsNull(Pageable pageable);

    Page<Company> findByHubIdAndDeletedAtIsNull(UUID id, Pageable pageable);
}
