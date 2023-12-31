package com.coinsimulation.upbit;

import com.coinsimulation.dto.common.TicketDto;
import com.coinsimulation.entity.coin.Bitcoin;
import com.coinsimulation.entity.coin.Ethereum;
import com.coinsimulation.repository.coin.BitcoinRepository;
import com.coinsimulation.repository.coin.EthereumRepository;
import com.coinsimulation.service.TicketService;
import com.coinsimulation.upbit.dto.FormatData;
import com.coinsimulation.upbit.dto.TickerData;
import com.coinsimulation.upbit.dto.TicketData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class UpbitTickerHandler implements WebSocketHandler {
    private final BitcoinRepository bitcoinRepository;
    private final EthereumRepository ethereumRepository;
    private final TicketService ticketService;
    private final String body;
    private final ObjectMapper camelOM;
    private final ObjectMapper snakeOM;
    private final String ERROR_DUPLICATION = "ID is duplicated";
    private final String ERROR_429 = "429 : too many request";

    public UpbitTickerHandler(BitcoinRepository bitcoinRepository, EthereumRepository ethereumRepository, TicketService ticketService) {
        this.camelOM = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        this.snakeOM = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.bitcoinRepository = bitcoinRepository;
        this.ethereumRepository = ethereumRepository;
        this.body = makeBody();
        this.ticketService = ticketService;
    }

    private String makeBody() {
        TicketData ticketData = new TicketData("test example");

        TickerData tickerData = new TickerData();

        List<String> codes = new ArrayList<>();
        codes.add("KRW-BTC");

        tickerData.setType("ticker");
        tickerData.setCodes(codes);
        tickerData.setOnlySnapshot(true);
        tickerData.setOnlyRealtime(false);

        FormatData formatData = new FormatData("DEFAULT");

        List<Object> dataList = new ArrayList<>();
        dataList.add(ticketData);
        dataList.add(tickerData);
        dataList.add(formatData);
        try {
            return camelOM.writeValueAsString(dataList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("현재 코인 가격 요청 : " + body);
        Flux.interval(Duration.ofMillis(600L))
                .filter(o -> session.isOpen())
                .flatMap(tick ->
                        session.send(Mono.just(session.textMessage(body)))
                                .onErrorStop())
                .subscribe();
        ticketService.setFlux(streamTickets(session));
        return ticketService.getFlux()
                .flatMap(payload -> {
                            if (payload.getCode().equals(Bitcoin.COIN_TYPE)) {
                                return bitcoinRepository.insert(Bitcoin.fromTicket(payload));
                            }
                            if (payload.getCode().equals(Ethereum.COIN_TYPE)) {
                                return ethereumRepository.insert(Ethereum.fromTicket(payload));
                            }
                            return Mono.empty(); // If none of the conditions are met, emit an empty Mono.
                        }
                )
                .onErrorContinue((throwable, o) -> {
                    if (throwable instanceof DuplicateKeyException) {
                        log.info(ERROR_DUPLICATION);
                        return;
                    }
                    if (throwable instanceof UnrecognizedPropertyException) {
                        log.error(ERROR_429);
                        //retryWhen 발동시키기 위한 예외발생.
                        throw new RuntimeException("429");
                    }
                    throwable.printStackTrace();
                })
                .then();
    }

    private Flux<TicketDto> streamTickets(WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .handle((payload, sink) -> {
                    try {
//                        log.info(payload);..
                        TicketDto ticketDto = snakeOM.readValue(payload, TicketDto.class);
                        ticketDto.setId(LocalDateTime.now().toString());
                        sink.next(ticketDto);
                        log.debug("received : " + ticketDto);
//                        log.info("ticket received");
                    } catch (JsonProcessingException e) {
                        log.error("json 변환 실패");
                        sink.error(e);
                    }
                })
                .cast(TicketDto.class);
    }
}
