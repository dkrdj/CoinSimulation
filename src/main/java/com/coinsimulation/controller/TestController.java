package com.coinsimulation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("test")
public class TestController {
    @GetMapping("1")
    public ResponseEntity<Flux<String>> test() {
        return ResponseEntity.ok(Flux.interval(Duration.ofMillis(300L))
                .map(tick -> "test" + (tick + 20) + "\n")
                .take(10));
    }

    @GetMapping("2")
    public Mono<Long> test2(@SessionAttribute("user") Long userId) {
        return Mono.just(userId);
    }
}
