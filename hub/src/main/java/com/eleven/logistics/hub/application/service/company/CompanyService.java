package com.eleven.logistics.hub.application.service.company;

import com.eleven.logistics.hub.application.dto.company.CompanyDto;
import com.eleven.logistics.hub.application.dto.company.CompanyResponseDto;
import com.eleven.logistics.hub.domain.entity.company.Company;
import com.eleven.logistics.hub.domain.entity.hub.Hub;
import com.eleven.logistics.hub.domain.repository.company.CompanyRepository;
import com.eleven.logistics.hub.domain.repository.hub.HubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final HubRepository hubRepository;

    @Transactional
    public CompanyResponseDto createCompany(CompanyDto dto) {
        Hub hubId = hubRepository.findById(dto.getHubId())
                .orElseThrow(()->new IllegalArgumentException("해당 허브를 찾을 수 없습니다."));

        if(hubRepository.existsByAddress(dto.getAddress())) {
            throw new IllegalArgumentException("이미 업체가 존재합니다.");
        }

        Company company = Company.create(
                dto.getName(),dto.getAddress(),
                dto.getType(),hubId);

        companyRepository.save(company);

        return CompanyResponseDto.of(company);
    }

    @Transactional(readOnly = true)
    public CompanyResponseDto findByCompanyId(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(()-> new IllegalArgumentException("찾으시는 업체가 없습니다."));

        if(company.getDeletedAt() != null){
            throw new IllegalArgumentException("해당 업체는 사라졌습니다.");
        }

        return CompanyResponseDto.of(company);
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponseDto> findByAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "deletedAt"));

        Page<Company> companies = companyRepository.findByDeletedAtIsNull(pageable);

        if(companies.isEmpty()){
            throw new IllegalArgumentException("업체를 찾을 수 없습니다.");
        }
        return companies.map(CompanyResponseDto::of);
    }

    @Transactional
    public CompanyResponseDto updateCompany(UUID companyId, CompanyDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(()-> new IllegalArgumentException("해당 업체를 찾을 수 없습니다."));

        Hub hubId = hubRepository.findById(dto.getHubId())
                .orElseThrow(()-> new IllegalArgumentException("찾으시는 허브가 없습니다."));

        if(company.getDeletedAt() != null){
            throw new IllegalArgumentException("해당 업체는 사라졌습니다.");
        }

        company.update(
                dto.getName(),dto.getAddress(),
                dto.getType(),hubId);

        return CompanyResponseDto.of(company);
    }

    @Transactional
    public void deleteCompany(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(()->new IllegalArgumentException("찾으시는 업체가 없습니다."));

        if (company.getDeletedAt() != null) {
            throw new IllegalArgumentException("이미 사리진 업체입니다.");
        }

        company.delete("admin");
    }
}
