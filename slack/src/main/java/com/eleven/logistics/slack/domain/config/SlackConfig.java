package com.eleven.logistics.slack.domain.config;

import org.json.JSONArray;

public interface SlackConfig {

    String getUserIdByName(String username);

    JSONArray getAllSlackUsers();

    String sendMessage(String userId, String message);
}
