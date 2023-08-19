package com.coinsimulation.service;

import com.coinsimulation.entity.Ticket;
import com.coinsimulation.repository.TicketRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
@Slf4j
public class UpbitServiceImpl implements UpbitService {
    private final ObjectMapper mapper;
    private final TicketRepository ticketRepository;

    @Override
    public void saveTicket(String resp) {
        Ticket ticket = null;
        try {
            ticket = mapper.readValue(resp, Ticket.class);
        } catch (JsonProcessingException e) {
            log.warn("object mapper 변환 실패");
        }
        Mono<Ticket> ticketMono = ticketRepository.save(ticket);
        ticketMono.subscribe(ticket1 -> log.info(ticket1.toString()));
    }
}
