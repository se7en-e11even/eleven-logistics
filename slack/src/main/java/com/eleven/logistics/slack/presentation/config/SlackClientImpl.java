package com.eleven.logistics.slack.presentation.config;

import com.eleven.logistics.slack.domain.config.SlackClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class SlackClientImpl implements SlackClient {

    // chat.scheduledMessage 메시지를 예약한다
    // chat.scheduledMessages.list 예약 메시지 보내기
    // chat.deleteScheduledMessage 예약 메시지 삭제
    // chat.update 메시지를 수정한다.

    private final RestTemplate restTemplate;

    public SlackClientImpl(RestTemplateBuilder builder) {
        restTemplate = builder.build();
    }

    @Value("${slack.token}")
    private String slackToken;


    public String getUserIdByName(String username) {
        JSONArray users = getAllSlackUsers();

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.has("profile") && user.getJSONObject("profile").has("real_name")) {
                String realName = user.getJSONObject("profile").getString("real_name");
                if (realName.equals(username)) {
                    return user.getString("id");
                }
            }
            if (user.has("profile") && user.getJSONObject("profile").has("display_name")) {
                String displayName = user.getJSONObject("profile").getString("display_name");
                if (displayName.equals(username)) {
                    return user.getString("id");
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
            throw new IllegalArgumentException("사용자를 불러오지 못했습니다.");
        }

        return jsonResponse.getJSONArray("members");
    }

    public String sendMessage(String userId, String message) {
        String openChannelUrl = "https://slack.com/api/conversations.open";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + slackToken);
        headers.add("Content-Type", "application/json");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("users", userId);

        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
        String response = restTemplate.postForObject(openChannelUrl, request, String.class);

        JSONObject jsonResponse = new JSONObject(response);

        if (!jsonResponse.getBoolean("ok")) {
            throw new IllegalArgumentException("채널을 열 수 없습니다.");
        }

        String channelId = jsonResponse.getJSONObject("channel").getString("id");

        // 채널에 메시지 전송
        String sendMessageUrl = "https://slack.com/api/chat.postMessage";

        jsonObject = new JSONObject();
        jsonObject.put("channel", channelId);
        jsonObject.put("text", message);

        request = new HttpEntity<>(jsonObject.toString(), headers);
        return restTemplate.postForObject(sendMessageUrl, request, String.class);
    }

    // Gemini API 응답에서 텍스트 추출하는 헬퍼 메서드
    public String extractTextFromGeminiResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    return parts.get(0).get("text");
                }
            }
            return "Gemini API 응답에서 텍스트를 추출할 수 없습니다.";
        } catch (Exception e) {
            throw new RuntimeException("Gemini API 응답 파싱 중 오류 발생", e);
        }
    }
}
