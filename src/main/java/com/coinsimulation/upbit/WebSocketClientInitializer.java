package com.coinsimulation.upbit;

import com.coinsimulation.repository.BitcoinRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;

@Service
@Slf4j
public class WebSocketClientInitializer {
    private final WebSocketClient client;
    private final BitcoinRepository bitcoinRepository;
    private final ObjectMapper om;
    private final String UPBIT_WEBSOCKET_URI = "wss://api.upbit.com/websocket/v1";
    private final URI uri = URI.create(UPBIT_WEBSOCKET_URI);

    public WebSocketClientInitializer(WebSocketClient client, BitcoinRepository bitcoinRepository, ObjectMapper om) {
        this.client = client;
        this.bitcoinRepository = bitcoinRepository;
        this.om = om;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void upbitListener() {
        client.execute(uri, new UpbitWebSocketHandler(om, bitcoinRepository))
                .subscribeOn(Schedulers.single())
                //429 에러면 throw 하기 때문에 여기서 retryWhen을 걸어줌.
                .doOnRequest(o -> log.info("웹소켓 연결중"))
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(2)))
                .subscribe();
    }
}