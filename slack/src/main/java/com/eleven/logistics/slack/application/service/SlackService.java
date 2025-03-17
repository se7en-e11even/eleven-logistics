package com.eleven.logistics.slack.application.service;

import com.eleven.logistics.slack.application.dto.SlackDto;
import com.eleven.logistics.slack.application.dto.SlackMessageResponse;
import com.eleven.logistics.slack.domain.entity.Slack;
import com.eleven.logistics.slack.domain.repository.SlackRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class SlackService {

    private final SlackRepository slackRepository;

    private final RestTemplate restTemplate;

    public SlackService(SlackRepository slackRepository, RestTemplateBuilder builder) {
        this.slackRepository = slackRepository;
        restTemplate = builder.build();
    }

    // chat.scheduledMessage 메시지를 예약한다
    // chat.scheduledMessages.list 예약 메시지 보내기
    // chat.deleteScheduledMessage 예약 메시지 삭제
    // chat.update 메시지를 수정한다.

    @Value("${slack.token}")
    private String slackToken;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Transactional
    public SlackMessageResponse sendMessageToAllUsers(SlackDto slackDto, String username) {
        String userId = getUserIdByName(slackDto.getUsername());

        if (userId == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        try {
            String result = sendMessage(userId, slackDto.getMessage());

            JSONObject jsonObject = new JSONObject(result);
            if(!jsonObject.getBoolean("ok")){
                log.error("메시지 전송에 실패했습니다.");
            }

            Slack slack = Slack.create(
                    username, slackDto.getMessage());
            slack.getCreatedBy(username);
            slackRepository.save(slack);

            return SlackMessageResponse.of(slack);
        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public String getUserIdByName(String username) {
        JSONArray users = getAllSlackUsers();

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.has("profile") && user.getJSONObject("profile").has("real_name")){
                String realName = user.getJSONObject("profile").getString("real_name");
                if(realName.equals(username)){
                    String userId = user.getString("id");
                    return userId;
                }
            }
        }
        return null;
    }

    public JSONArray getAllSlackUsers() {
        String url = "https://slack.com/api/users.list";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + slackToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        JSONObject jsonResponse = new JSONObject(response.getBody());
        if (!jsonResponse.getBoolean("ok")) {
            throw new IllegalArgumentException("사용자를 불러오지 못했습니다 : " + jsonResponse.getString("error"));
        }

        return jsonResponse.getJSONArray("members");
    }

    public String sendMessage(String userId, String message) {
//        String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="
//                + geminiApiKey;
        String openChannelUrl = "https://slack.com/api/conversations.open";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + slackToken);
        headers.add("Content-Type", "application/json");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("users", userId);

        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(openChannelUrl, request, String.class);

        JSONObject jsonResponse = new JSONObject(response.getBody());

        if (!jsonResponse.has("channel")) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다. : " + response.getBody());
        }

        String channelId = jsonResponse.getJSONObject("channel").getString("id");

        String sendMessageUrl = "https://slack.com/api/chat.postMessage"; // chat:write 권한을 설정하면 사용할 수 있다.

        jsonObject = new JSONObject();
        jsonObject.put("channel", channelId);
        jsonObject.put("text", message);

        request = new HttpEntity<>(jsonObject.toString(), headers);
        response = restTemplate.postForEntity(sendMessageUrl, request, String.class);

        return response.getBody();
    }
}
