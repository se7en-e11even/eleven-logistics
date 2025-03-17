package com.eleven.logistics.slack.domain.repository;

import com.eleven.logistics.slack.domain.entity.Slack;

// interface 계층에 존재하는 JpaRepository 를 분리하기 위한 interface 구현체
public interface SlackRepository {
    Slack save(Slack slack);

}
