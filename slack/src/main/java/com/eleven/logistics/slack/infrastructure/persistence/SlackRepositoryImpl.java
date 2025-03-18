package com.eleven.logistics.slack.infrastructure.persistence;

import com.eleven.logistics.slack.domain.entity.Slack;
import com.eleven.logistics.slack.domain.repository.SlackRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// JpaRepository 를 interface 계층에 위시 시키는 방법
@Repository
public interface SlackRepositoryImpl extends JpaRepository<Slack, UUID>, SlackRepository {

}
