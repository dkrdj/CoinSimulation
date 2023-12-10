package com.coinsimulation.controller;

import com.coinsimulation.sse.UserChannels;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("sse")
public class SSEController {
    private final UserChannels channels;

    @GetMapping
    public Flux<ServerSentEvent<String>> connect(@SessionAttribute("user") Long userId) {
        Flux<String> userStream = channels.connect(userId).toFlux();
        Flux<String> tickStream = Flux.interval(Duration.ofSeconds(3))
                .map(tick -> "HEARTBEAT");
        return Flux.merge(userStream, tickStream)
                .map(str -> ServerSentEvent.builder(str).build());
    }
}
