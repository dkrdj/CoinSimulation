package com.coinsimulation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("test")
public class TestController {
    @GetMapping("1")
    public Flux<String> test() {
        return Flux.interval(Duration.ofMillis(300L))
                .map(tick -> "test" + (tick + 20) + "\n")
                .take(10);
    }
}
