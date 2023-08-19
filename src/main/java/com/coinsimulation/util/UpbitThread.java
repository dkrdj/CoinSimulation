package com.coinsimulation.util;

import com.coinsimulation.service.UpbitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpbitThread {
    private final UpbitService upbitService;

    private final String ACCESS_KEY = "xgzZBPrL0mdUKV88JElrU1tViKVdxbivQw8mPuaZ";
    private final String SECRET_KEY = "4IpEyMxOIBDD0swtnvlIPAMtEESKFmyNeaWb045K";

    public void run() {
        startWithCreation();
    }

    private void startWithCreation() {
        try {
            WebSocketClient client = new ReactorNettyWebSocketClient();
            HttpHeaders header = new HttpHeaders();
            URI uri = URI.create("wss://api.upbit.com/websocket/v1");
            receiveCoinData(client, header, uri);
        } catch (Exception e) {
            startWithCreation();
        }
    }

    private void receiveCoinData(WebSocketClient client, HttpHeaders header, URI uri) {
        while (true) {
            client.execute(
                    uri,
                    header,
                    this::handleWebSocketSession
            ).block(Duration.ofMillis(1000L));
        }
    }

    private Mono<Void> handleWebSocketSession(WebSocketSession session) {
        String body = """
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
        return session.send(Mono.just(session.textMessage(body)))
                .thenMany(session.receive().map(WebSocketMessage::getPayload))
                .doOnNext(message -> {
                    String result = message.toString(StandardCharsets.UTF_8);
                    upbitService.saveTicket(result);
                })
                .then();
    }

}
