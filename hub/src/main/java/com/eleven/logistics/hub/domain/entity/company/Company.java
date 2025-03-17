package com.eleven.logistics.hub.domain.entity.company;

import com.eleven.logistics.hub.domain.entity.BaseSystemFieldEntity;
import com.eleven.logistics.hub.domain.entity.hub.Hub;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_company")
@Builder(access = AccessLevel.PRIVATE)
public class Company extends BaseSystemFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CompanyType type;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "name", nullable = false)
    private String name;

    public enum CompanyType{
        PRODUCER_COMPANY, RECEIVER_COMPANY
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_id")
    private Hub hub;

    public static Company create(String name, String address, CompanyType type, Hub hubId, String username) {
        return Company.builder()
                .name(name)
                .address(address)
                .type(type)
                .username(username)
                .hub(hubId)
                .build();
    }
    public void update(String name, String address, CompanyType type, Hub hubId, String username) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.hub = hubId;
        this.username = username;
    }
}
