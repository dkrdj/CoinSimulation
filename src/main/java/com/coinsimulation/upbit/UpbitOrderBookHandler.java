package com.coinsimulation.upbit;

import com.coinsimulation.service.OrderBookService;
import com.coinsimulation.upbit.dto.FormatData;
import com.coinsimulation.upbit.dto.OrderBook;
import com.coinsimulation.upbit.dto.TickerData;
import com.coinsimulation.upbit.dto.TicketData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Slf4j
public class UpbitOrderBookHandler implements WebSocketHandler {
    private final String body;
    private final ObjectMapper camelOM;
    private final ObjectMapper snakeOM;
    private final OrderBookService orderBookService;

    public UpbitOrderBookHandler(OrderBookService orderBookService) {
        this.camelOM = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        this.snakeOM = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.body = makeBody();
        this.orderBookService = orderBookService;
    }

    private String makeBody() {
        TicketData ticketData = new TicketData("test example");
        TickerData tickerData = new TickerData();

        tickerData.setType("orderbook");
        tickerData.setCodes(List.of("KRW-BTC"));
        tickerData.setOnlySnapshot(false);
        tickerData.setOnlyRealtime(true);

        FormatData formatData = new FormatData("DEFAULT");

        try {
            return camelOM.writeValueAsString(List.of(ticketData, tickerData, formatData));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("호가 요청 : " + body);
//        System.out.println(body);
        session.send(Mono.just(session.textMessage(body))).subscribe();
        return orderBookService.setFlux(StreamTickets(session))
                .thenMany(orderBookService.getFlux())
                .onErrorContinue((throwable, o) -> {
                    throwable.printStackTrace();
                })
                .then();
//        return OrderBookService.getFlux("KRW-BTC").then();
//        return flux
//                .onErrorContinue((throwable, o) -> {
//                    throwable.printStackTrace();
//                })
//                .then();
    }

    private Flux<OrderBook> StreamTickets(WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
//                .doOnNext(System.out::println)
                .handle((payload, sink) -> {
                    try {
//                        log.info(payload);
                        OrderBook orderBookDto = snakeOM.readValue(payload, OrderBook.class);
                        sink.next(orderBookDto);
//                        log.info("orderbook received : " + orderBookDto);
                    } catch (JsonProcessingException e) {
                        log.error("json 변환 실패");
                        sink.error(e);
                    }
                })
                .onErrorContinue(
                        (throwable, o) -> throwable.printStackTrace()
                )
                .cast(OrderBook.class);
    }
}
