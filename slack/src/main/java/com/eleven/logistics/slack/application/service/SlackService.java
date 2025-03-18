package com.eleven.logistics.slack.application.service;

import com.eleven.logistics.slack.application.dto.PageResponseDto;
import com.eleven.logistics.slack.application.dto.SlackDto;
import com.eleven.logistics.slack.application.dto.SlackMessageResponse;
import com.eleven.logistics.slack.domain.entity.Slack;
import com.eleven.logistics.slack.domain.repository.SlackRepository;
import com.eleven.logistics.slack.presentation.config.SlackConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SlackService {

    private final SlackRepository slackRepository;

    private final SlackConfig slackConfig;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Transactional
    public SlackMessageResponse sendMessageToUser(SlackDto slackDto, String username) {
        try {
            String userId = slackConfig.getUserIdByName(slackDto.getUsername());

            if (userId == null) {
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다");
            }
            String result = slackConfig.sendMessage(userId, slackDto.getMessage());

            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.getBoolean("ok")) {
                throw new IllegalArgumentException("메시지 전송에 실패했습니다.");
            }

            Slack slack = Slack.create(
                    slackDto.getUsername(), slackDto.getMessage());
            slack.getCreatedBy(username);
            slackRepository.save(slack);

            return SlackMessageResponse.of(slack);
        } catch (Exception e) {
            throw new RuntimeException("메시지 전송 중 오류가 발생했습니다", e);
        }
    }

    @Transactional(readOnly = true)
    public SlackMessageResponse findBySlackId(UUID slackId) {
        Slack slack = slackRepository.findById(slackId)
                .orElseThrow(()->new IllegalArgumentException("찾으시는 메시지가 없습니다."));

        return SlackMessageResponse.of(slack);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "findByAll", key = "#page+'-'+#size")
    public PageResponseDto<SlackMessageResponse> findByAll(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Slack> slack = slackRepository.findByDeletedAtIsNull(pageable);

        if (slack.isEmpty()){
            throw new IllegalArgumentException("메시지가 존재하지 않습니다.");
        }
        Page<SlackMessageResponse> pageDto = slack.map(SlackMessageResponse::of);

        return PageResponseDto.of(pageDto);
    }

    @Transactional
    public SlackMessageResponse updateSlackMessage(UUID slackId, SlackDto dto, String username) {
        Slack slack = slackRepository.findById(slackId)
                .orElseThrow(()->new IllegalArgumentException("찾으시는 메시지가 없습니다."));
        return null;
    }
}