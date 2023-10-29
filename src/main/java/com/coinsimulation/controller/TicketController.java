package com.coinsimulation.controller;

import com.coinsimulation.dto.common.TicketDto;
import com.coinsimulation.service.TicketService;
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
public class TicketController {
    private final TicketService ticketService;

    @MessageMapping("ticket.{code}")
    public Flux<ResponseEntity<TicketDto>> subscribeTicket(@DestinationVariable("code") String code) {
        return this.ticketService.subscribeTicket(code)
                .map(ResponseEntity::ok);
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<String>> handleException(Exception e) {
        return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
    }
}
