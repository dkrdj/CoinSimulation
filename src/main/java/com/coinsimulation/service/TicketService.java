package com.coinsimulation.service;

import com.coinsimulation.dto.common.TicketDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TicketService {
    private Map<String, Double> currentPriceMap = new ConcurrentHashMap<>();

    private Flux<TicketDto> flux = Flux.empty();


    public Flux<TicketDto> getFlux() {
        return this.flux;
    }

    public void setFlux(Flux<TicketDto> ticketFlux) {
        this.flux = ticketFlux.doOnNext(ticketDto ->
                        currentPriceMap.put(ticketDto.getCode(), ticketDto.getAccTradePrice())
                )
                .share();
    }

    public Double getCurrentPrice(String code) {
        return currentPriceMap.get(code);
    }


    public Flux<TicketDto> subscribeTicket(String code) {
        return this.flux;
    }

}


