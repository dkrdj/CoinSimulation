package com.coinsimulation.security;

import java.util.Map;

public class KakaoUserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, String> properties;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        properties = (Map<String, String>) attributes.get("properties");
    }


    public Long getProviderId() {
        return (Long) attributes.get("id");
    }


    public String getNickname() {
        return properties.get("nickname");
    }


    public String getProfile() {
        return properties.get("profile_image");
    }
}