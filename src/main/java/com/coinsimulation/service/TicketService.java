package com.coinsimulation.service;

import com.coinsimulation.dto.common.TicketDto;
import com.coinsimulation.dto.request.TicketRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TicketService {

    private static Flux<TicketDto> flux = Flux.empty();


    public static Flux<TicketDto> getFlux() {
        return flux;
    }

    public static void setFlux(Flux<TicketDto> ticketFlux) {
        flux = ticketFlux.share();
    }

    public Flux<TicketDto> subscribeTicket(TicketRequest ticketRequestMono) {
        return this.flux;
    }

}


