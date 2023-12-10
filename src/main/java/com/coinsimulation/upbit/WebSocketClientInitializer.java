package com.coinsimulation.upbit;

import com.coinsimulation.repository.coin.BitcoinRepository;
import com.coinsimulation.repository.coin.EthereumRepository;
import com.coinsimulation.service.OrderBookService;
import com.coinsimulation.service.TicketService;
import com.coinsimulation.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketClientInitializer {
    private final WebSocketClient client;
    private final BitcoinRepository bitcoinRepository;
    private final EthereumRepository ethereumRepository;
    private final OrderBookService orderBookService;
    private final TicketService ticketService;
    private final TradeService tradeService;
    private final String UPBIT_WEBSOCKET_URI = "wss://api.upbit.com/websocket/v1";
    private final URI uri = URI.create(UPBIT_WEBSOCKET_URI);

    @EventListener(ContextRefreshedEvent.class)
    public void upbitListener() {
        client.execute(uri, new UpbitTickerHandler(bitcoinRepository, ethereumRepository, ticketService))
                .subscribeOn(Schedulers.single())
                //429 에러면 throw 하기 때문에 여기서 retryWhen을 걸어줌.
                .doOnRequest(o -> log.info("가격 웹소켓 연결중"))
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(2)))
                .subscribe();
        client.execute(uri, new UpbitOrderBookHandler(orderBookService))
                .subscribeOn(Schedulers.single())
                .doOnRequest(o -> log.info("호가 웹소켓 연결중"))
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(2)))
                .subscribe();
        client.execute(uri, new UpbitTradeHandler(tradeService))
                .subscribeOn(Schedulers.single())
                .doOnRequest(o -> log.info("체결 웹소켓 연결중"))
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(2)))
                .subscribe();
    }

}