package com.coinsimulation.service;

import com.coinsimulation.upbit.dto.Trade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TradeService {
    private static Flux<Trade> flux = Flux.empty();

    public static Flux<Trade> getFlux() {
        return flux;
    }

    public static Mono<Void> setFlux(Flux<Trade> orderBookFlux) {
        flux = orderBookFlux.share();
        return Mono.empty();
    }

    public Flux<Trade> subscribeOrderBook(String code) {
        return this.flux;
    }
}
