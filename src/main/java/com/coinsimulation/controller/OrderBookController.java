package com.coinsimulation.controller;

import com.coinsimulation.service.OrderBookService;
import com.coinsimulation.upbit.dto.OrderBook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class OrderBookController {
    private final OrderBookService orderBookService;

    @MessageMapping("orderbook.{code}")
    public Flux<ResponseEntity<OrderBook>> subscribeOrderBook(@DestinationVariable("code") String code) {
        return this.orderBookService.subscribeOrderBook(code)
                .map(ResponseEntity::ok);
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<String>> handleException(Exception e) {
        return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
    }
}
