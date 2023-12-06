package com.coinsimulation.service;

import com.coinsimulation.upbit.dto.OrderBook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class OrderBookService {
    private Flux<OrderBook> flux = Flux.empty();

    public Flux<OrderBook> getFlux() {
        return this.flux;
    }

    public Mono<Void> setFlux(Flux<OrderBook> orderBookFlux) {
        this.flux = orderBookFlux
                //취소된 건 처리하는 로직 추가 필요
                .share();
        return Mono.empty();
    }


    public Flux<OrderBook> subscribeOrderBook(String code) {
        return this.flux;
    }
}

