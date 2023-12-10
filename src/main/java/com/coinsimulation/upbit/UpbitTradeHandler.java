package com.coinsimulation.upbit;

import com.coinsimulation.service.TradeService;
import com.coinsimulation.upbit.dto.FormatData;
import com.coinsimulation.upbit.dto.TickerData;
import com.coinsimulation.upbit.dto.TicketData;
import com.coinsimulation.upbit.dto.Trade;
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
public class UpbitTradeHandler implements WebSocketHandler {
    private final String body;
    private final ObjectMapper camelOM;
    private final ObjectMapper snakeOM;
    private final TradeService tradeService;

    public UpbitTradeHandler(TradeService tradeService) {
        this.camelOM = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        this.snakeOM = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.tradeService = tradeService;
        this.body = makeBody();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("체결 요청 : " + body);
        session.send(Mono.just(session.textMessage(body))).subscribe();
        return tradeService.setFlux(StreamTickets(session))
                .thenMany(tradeService.getFlux())
                .onErrorContinue((throwable, o) -> {
                    throwable.printStackTrace();
                })
                .then();
    }

    private Flux<Trade> StreamTickets(WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
//                .doOnNext(System.out::println)
                .handle((payload, sink) -> {
                    try {
//                        log.info(payload);
                        Trade trade = snakeOM.readValue(payload, Trade.class);
                        sink.next(trade);
//                        log.info("trade received : " + trade);
                    } catch (JsonProcessingException e) {
                        log.error("json 변환 실패");
                        sink.error(e);
                    }
                })
                .onErrorContinue(
                        (throwable, o) -> throwable.printStackTrace()
                )
                .cast(Trade.class);
    }

    private String makeBody() {
        TicketData ticketData = new TicketData("test example");
        TickerData tickerData = new TickerData();

        tickerData.setType("trade");
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
}
