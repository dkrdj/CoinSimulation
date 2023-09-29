package com.coinsimulation.controller;

import com.coinsimulation.dto.RequestDto;
import com.coinsimulation.entity.TicketDto;
import com.coinsimulation.service.PublishingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class TicketController {
    private final PublishingService publishingService;

    @MessageMapping("ticket")
    public Flux<TicketDto> subscribeTicket(RequestDto requestDtoMono) {
        return this.publishingService.subscribeTicket(requestDtoMono);
    }
}
