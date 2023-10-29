package com.coinsimulation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

@Configuration
public class WebSocketClientConfig {
    @Bean
    public WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }

    @Bean
    public ObjectMapper snakeObjectMapper() {
        return new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

}
