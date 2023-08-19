package com.coinsimulation.config;

import com.coinsimulation.service.UpbitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class SchedulingConfig {
    private final UpbitService upbitService;
    private final String ACCESS_KEY = "xgzZBPrL0mdUKV88JElrU1tViKVdxbivQw8mPuaZ";
    private final String SECRET_KEY = "4IpEyMxOIBDD0swtnvlIPAMtEESKFmyNeaWb045K";
    private final URI uri = URI.create("wss://api.upbit.com/websocket/v1");
    private final String body = """
            [
              {
                "ticket": "test example"
              },
              {
                "type": "ticker",
                "codes": [
                  "KRW-BTC",
                  "KRW-ETH"
                ]
              },
              {
                "format": "DEFAULT"
              }
            ]""";

    private final WebSocketClient client = new ReactorNettyWebSocketClient();

    private final HttpHeaders header = new HttpHeaders();

    @Scheduled(fixedRate = 1000L)
    private void receiveCoinData() {
        client.execute(
                uri,
                header,
                this::handleWebSocketSession
        ).block();
    }

    private Mono<Void> handleWebSocketSession(WebSocketSession session) {

        return session.send(Mono.just(session.textMessage(body)))
                .thenMany(session.receive().map(WebSocketMessage::getPayload))
                .doOnNext(message -> {
                    String result = message.toString(StandardCharsets.UTF_8);
                    upbitService.saveTicket(result);
                })
                .then();
    }
}