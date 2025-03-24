package com.eleven.logistics.hub.infrastructure.persistence.company;

import com.eleven.logistics.hub.domain.entity.company.Company;
import com.eleven.logistics.hub.domain.repository.company.CompanyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompanyRepositoryImpl extends JpaRepository<Company, UUID>, CompanyRepository {
}
