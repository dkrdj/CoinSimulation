package com.coinsimulation.service;

import com.coinsimulation.dto.RequestDto;
import com.coinsimulation.dto.TicketDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class PublishingService {

    private static Flux<TicketDto> flux;

    public PublishingService() {
        this.flux = Flux.empty();
    }

    public static Flux<TicketDto> getFlux() {
        return flux;
    }

    public static void setFlux(Flux<TicketDto> ticketFlux) {
        flux = ticketFlux.share();
    }

    public Flux<TicketDto> subscribeTicket(RequestDto requestDtoMono) {
        return this.flux
                .filter(ticketDto -> ticketDto.getCode().equals(requestDtoMono.getCode()))
                .switchIfEmpty(Flux.error(new IllegalArgumentException("no code in db")))
                .doFirst(() -> log.info("subscribe"));
    }

}


