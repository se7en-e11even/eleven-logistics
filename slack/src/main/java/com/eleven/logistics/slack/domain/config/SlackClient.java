package com.eleven.logistics.slack.domain.config;

import org.json.JSONArray;

import java.util.Map;

public interface SlackClient {

    String getUserIdByName(String username);

    JSONArray getAllSlackUsers();

    String sendMessage(String userId, String message);

    String extractTextFromGeminiResponse(Map<String, Object> response);
}
