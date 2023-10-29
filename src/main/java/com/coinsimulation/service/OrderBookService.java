package com.coinsimulation.service;

import com.coinsimulation.upbit.dto.OrderBook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class OrderBookService {
    //    private static Map<String, Flux<OrderBook>> fluxMap;
    private static Flux<OrderBook> flux = Flux.empty();
//    public OrderBookService() {
//        fluxMap = new ConcurrentHashMap<>();
//    }

    public static Flux<OrderBook> getFlux() {
        return flux;
    }

    public static Mono<Void> setFlux(Flux<OrderBook> orderBookFlux) {
        flux = orderBookFlux.share();
        return Mono.empty();
    }

    public Flux<OrderBook> subscribeOrderBook(String code) {
        return this.flux;
    }
}

