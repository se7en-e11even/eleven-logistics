package com.eleven.logistics.slack.domain.repository;

import com.eleven.logistics.slack.domain.entity.Slack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

// interface 계층에 존재하는 JpaRepository 를 분리하기 위한 interface 구현체
public interface SlackRepository {
    Slack save(Slack slack);

    Optional<Slack> findById(UUID slackId);

    Page<Slack> findByDeletedAtIsNull(Pageable pageable);
}
