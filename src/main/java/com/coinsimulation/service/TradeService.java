package com.coinsimulation.service;

import com.coinsimulation.upbit.dto.Trade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class TradeService {
    private final ExecutionService executionService;
    private Flux<Trade> flux = Flux.empty();

    public Flux<Trade> getFlux() {
        return this.flux;
    }

    public Mono<Void> setFlux(Flux<Trade> tradeFlux) {
        this.flux = tradeFlux
                //executionService에 보내서 처리해야함.
                .flatMap(trade -> executionService.executeTrade(trade))
                .share();
        return Mono.empty();
    }

    public Flux<Trade> subscribeOrderBook(String code) {
        return this.flux;
    }
}
