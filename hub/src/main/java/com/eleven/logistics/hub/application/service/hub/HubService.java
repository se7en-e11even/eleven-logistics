package com.eleven.logistics.hub.application.service.hub;

import com.eleven.logistics.hub.application.dto.PageResponseDto;
import com.eleven.logistics.hub.application.dto.company.CompanyResponseDto;
import com.eleven.logistics.hub.application.dto.hub.HubDto;
import com.eleven.logistics.hub.application.dto.hub.HubResponseDto;
import com.eleven.logistics.hub.domain.entity.company.Company;
import com.eleven.logistics.hub.domain.entity.hub.Hub;
import com.eleven.logistics.hub.domain.repository.company.CompanyRepository;
import com.eleven.logistics.hub.domain.repository.hub.HubRepository;
import com.eleven.logistics.hub.infrastructure.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubService {

    private final HubRepository hubRepository;
    private final CompanyRepository companyRepository;
    private final GeocodingService geocodingService;

    @Transactional
    public HubResponseDto createHub(HubDto dto) {
        if(hubRepository.existsByAddress(dto.getAddress())){
            throw new IllegalArgumentException("해당 위치에 이미 허브가 존재합니다.");
        }

        Hub hub = Hub.create(
                dto.getName(),
                dto.getAddress()
        );
        double[] coordinates = geocodingService.getCoordinates(hub.getAddress());

        if (coordinates != null && coordinates.length == 2) {
            hub.updateCoordinates(coordinates[0], coordinates[1]);
        } else {
            throw new IllegalArgumentException("위도, 경도를 찾을 수 없습니다.");
        }
        hubRepository.save(hub);

        return HubResponseDto.of(hub);
    }

    @Transactional(readOnly = true)
    // cacheNames = 이 API 로 만들어질 캐시를 지칭하는 이름, key = 캐시 데이터를 구분하기 위한 값
    // cache - aside 전략
    @Cacheable(cacheNames = "findByHubId", key = "#hubId + '-' + #page+'-'+#size")
    public HubResponseDto findByHubId(UUID hubId, int page, int size) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Company> companies = companyRepository.findByHubIdAndDeletedAtIsNull(hub.getId(), pageable);

        Page<CompanyResponseDto> pageDto = companies
                .map(CompanyResponseDto::of);

        PageResponseDto<CompanyResponseDto> company = PageResponseDto.of(pageDto);

        if(hub.getDeletedAt() != null){
            throw new IllegalArgumentException("허브를 찾을 수 없습니다.");
        }

        return HubResponseDto.listOf(hub, company);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "findByALL", key = "#page+ '-'+ #size")
    public PageResponseDto<HubResponseDto> findByAll(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Hub> hubs = hubRepository.findByDeletedAtIsNull(pageable);

        if(hubs.isEmpty()) {
            throw new IllegalArgumentException("허브를 찾을 수 없습니다.");
        }
        Page<HubResponseDto> pageDto = hubs.map(HubResponseDto::of);
        return PageResponseDto.of(pageDto);
    }

    @Transactional
    public HubResponseDto updateHub(UUID hubId, HubDto dto) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));

        if(hubRepository.existsByAddress(dto.getAddress())){
            throw new IllegalArgumentException("해당 위치에 이미 허브가 존재합니다.");
        }

        double[] coordinates = geocodingService.getCoordinates(dto.getAddress());

        if (coordinates != null && coordinates.length == 2) {
            hub.updateCoordinates(coordinates[0], coordinates[1]);
        } else {
            throw new IllegalArgumentException("실제 주소 정보가 없습니다.");
        }

        hub.update(dto.getName(), dto.getAddress());

        return HubResponseDto.of(hub);
    }

    @Transactional
    public void deleteHub(UUID hubId) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));

        if (hub.getDeletedAt() != null) {
            throw new IllegalArgumentException("허브를 찾을 수 없습니다.");
        }

        hub.delete("admin");
    }
}
