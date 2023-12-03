package com.coinsimulation.service;

import com.coinsimulation.dto.common.TicketDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TicketService {

    private Flux<TicketDto> flux = Flux.empty();


    public Flux<TicketDto> getFlux() {
        return this.flux;
    }

    public void setFlux(Flux<TicketDto> ticketFlux) {
        this.flux = ticketFlux.share();
    }

    public Flux<TicketDto> subscribeTicket(String code) {
        return this.flux;
    }

}


