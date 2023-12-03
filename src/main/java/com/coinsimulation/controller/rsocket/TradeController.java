package com.coinsimulation.controller.rsocket;

import com.coinsimulation.service.TradeService;
import com.coinsimulation.upbit.dto.Trade;
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
public class TradeController {
    private final TradeService tradeService;

    @MessageMapping("trade.{code}")
    public Flux<ResponseEntity<Trade>> subscribeTrade(@DestinationVariable("code") String code) {
        return this.tradeService.subscribeOrderBook(code)
                .map(ResponseEntity::ok);
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<String>> handleException(Exception e) {
        return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
    }
}
