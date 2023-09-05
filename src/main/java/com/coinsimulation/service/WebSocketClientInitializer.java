package com.coinsimulation.service;

import com.coinsimulation.entity.Ticket;
import com.coinsimulation.repository.TicketRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketClientInitializer {
    private final WebSocketClient client;
    private final TicketRepository ticketRepository;
    private final ObjectMapper om;
    private final String body = """
            [
              {
                "ticket": "test example"
              },
              {
                "type": "ticker",
                "codes": [
                  "KRW-BTC"
                ],
                "isOnlySnapshot": "true"
              },
              {
                "format": "DEFAULT"
              }
            ]""";
    private URI uri = URI.create("wss://api.upbit.com/websocket/v1");

    @EventListener(ContextRefreshedEvent.class)
    public void upbitListener() {
        System.out.println("contextRefreshed");

        Flux.interval(Duration.ofMillis(200L))
                .flatMap(tick -> client.execute(uri, connectAndReceive(tick + "id")))
                .subscribe();
    }

    public WebSocketHandler connectAndReceive(String id) {
        return session -> session.send(Mono.just(session.textMessage(body)))
                .thenMany(session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .<Ticket>handle((payload, sink) -> {
                            try {
                                Ticket ticket = om.readValue(payload, Ticket.class);
                                sink.next(ticket.setId(id));
                            } catch (JsonProcessingException e) {
                                sink.error(new RuntimeException(e));
                            }
                        })
                        .flatMap(ticketRepository::insert)
                )
                .then();
    }
}