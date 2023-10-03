package com.coinsimulation.service;

import com.coinsimulation.dto.TicketDto;
import com.coinsimulation.dto.request.TicketRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TicketService {

    private static Flux<TicketDto> flux;

    public TicketService() {
        this.flux = Flux.empty();
    }

    public static Flux<TicketDto> getFlux() {
        return flux;
    }

    public static void setFlux(Flux<TicketDto> ticketFlux) {
        flux = ticketFlux.share();
    }

    public Flux<TicketDto> subscribeTicket(@RequestBody TicketRequest ticketRequestMono) {
        return this.flux
                .filter(ticketDto -> ticketDto.getCode().equals(ticketRequestMono.getCode()))
                .switchIfEmpty(Flux.error(new IllegalArgumentException("no code in db")))
                .doFirst(() -> log.info("subscribe"));
    }

}


