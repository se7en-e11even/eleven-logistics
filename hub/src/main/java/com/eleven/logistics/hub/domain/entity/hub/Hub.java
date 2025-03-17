package com.eleven.logistics.hub.domain.entity.hub;

import com.eleven.logistics.hub.domain.entity.BaseSystemFieldEntity;
import com.eleven.logistics.hub.domain.entity.company.Company;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_hub")
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Hub extends BaseSystemFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @OneToMany(mappedBy = "hub", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Company> companyList = new ArrayList<>();

    public static Hub create(String name, String address) {
        Hub hub = Hub.builder()
                .name(name)
                .address(address)
                .build();
        return hub;
    }

    public void update(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
