package com.coinsimulation.controller;

import com.coinsimulation.dto.TicketDto;
import com.coinsimulation.dto.request.TicketRequest;
import com.coinsimulation.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @MessageMapping("ticket")
    public Flux<ResponseEntity<TicketDto>> subscribeTicket(TicketRequest ticketRequestMono) {
        return this.ticketService.subscribeTicket(ticketRequestMono)
                .map(ticket -> ResponseEntity.ok(ticket));
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<String>> handleException(Exception e) {
        return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
    }
}
