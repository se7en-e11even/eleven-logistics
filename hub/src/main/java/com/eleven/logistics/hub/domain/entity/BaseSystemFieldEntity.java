package com.eleven.logistics.hub.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseSystemFieldEntity {

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false, nullable = false)
    private String createdBy = "admin";

    //    @UpdateTimestamp
    @Column(nullable = true)
    private LocalDateTime updatedAt;

    //    @LastModifiedBy
    @Column(nullable = true)
    private String updatedBy;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private String deletedBy;

    // CREATE 시에는 updateAt과 updateBy를 설정하지 않음
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = null;
        updatedBy = null;
        // CREATE 시에는 updateAt과 updateBy를 설정하지 않음
    }

    // UPDATE 시에만 updateAt과 updateBy를 갱신
    @PreUpdate
    public void preUpdate() {
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
            updatedBy = "admin";
        }
    }
    // 소프트 삭제 처리
    public void delete(String deletedBy) {
        this.deletedBy = deletedBy;
        this.deletedAt = LocalDateTime.now();
    }
}
