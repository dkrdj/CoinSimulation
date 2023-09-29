package com.coinsimulation.service;

import com.coinsimulation.dto.RequestDto;
import com.coinsimulation.entity.TicketDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class PublishingService {

    private static Flux<TicketDto> flux;

    public PublishingService() {
        flux = Flux.empty();
    }

    public static Flux<TicketDto> getFlux() {
        return flux;
    }

    public static void setFlux(Flux<TicketDto> ticketFlux) {
        flux = ticketFlux.share();
    }

    public Flux<TicketDto> subscribeTicket(RequestDto requestDtoMono) {
        return flux
                .filter(ticketDto -> ticketDto.getCode().equals(requestDtoMono.getCode()))
                .doOnNext(o -> System.out.println("subscribe"));
    }

}
