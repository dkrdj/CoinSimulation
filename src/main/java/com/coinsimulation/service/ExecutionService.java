package com.coinsimulation.service;

import com.coinsimulation.dto.response.ExecutionResponse;
import com.coinsimulation.entity.Asset;
import com.coinsimulation.entity.Execution;
import com.coinsimulation.entity.Order;
import com.coinsimulation.repository.AssetRepository;
import com.coinsimulation.repository.ExecutionRepository;
import com.coinsimulation.repository.OrderRepository;
import com.coinsimulation.repository.UserRepository;
import com.coinsimulation.sse.UserChannels;
import com.coinsimulation.upbit.dto.Trade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutionService {
    private final UserChannels userChannels;
    private final ExecutionRepository executionRepository;
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    public Flux<ExecutionResponse> selectExecution(Long userId) {
        return executionRepository.findByUserId(userId).map(Execution::toResponse);
    }

    @Transactional
    public Mono<Trade> executeTrade(Trade trade) {
        String gubun = trade.getAskBid().equals("ASK") ? "buy" : "sell";
//        log.info(trade.toString());
//        if (trade.getAskBid().equals("ASK")) {
//            log.info(String.valueOf(doubleNum.addAndGet(trade.getTradeVolume())));
//        }
        Flux<Execution> executionFlux = orderRepository.findOrdersForAsk(gubun, trade.getCode(), trade.getTradePrice())
                .transform(orderFlux -> orderFlux
                        .flatMap(order -> updateOrder(trade, order))
                );
        if (gubun.equals("buy")) {
            return executionFlux
                    .flatMap(execution -> assetRepository.findByUserIdAndCodeForUpdate(execution.getUserId(), execution.getCode())
                                    .switchIfEmpty(Mono.just(Asset.builder()
                                            .amount(0d)
                                            .averagePrice(0d)
                                            .code(execution.getCode())
                                            .userId(execution.getUserId())
                                            .build()))
//                        .doOnNext(asset -> log.info("asset average : " + asset.getAveragePrice() + " amount : " + asset.getAmount()))
                                    .doOnNext(asset -> asset.setAveragePrice(calculateAveragePrice(asset, execution)))
                                    .doOnNext(asset -> asset.setAmount(asset.getAmount() + execution.getAmount()))
//                        .doOnNext(asset -> log.info("asset average : " + asset.getAveragePrice() + " amount : " + asset.getAmount()))
                                    .flatMap(assetRepository::save)
                    )
                    .then(Mono.just(trade));
        } else {
            return executionFlux
                    .flatMap(
                            execution -> userRepository.findByIdForUpdate(execution.getUserId())
                                    .doOnNext(user -> user.setCash(user.getCash() + execution.getTotalPrice()))
                                    .flatMap(userRepository::save)
                    )
                    .then(Mono.just(trade));
        }
    }

    private Mono<Execution> updateOrder(Trade trade, Order order) {
        Double restAmount = order.getAmount();
        if (Double.compare(restAmount, Math.min(trade.getTradeVolume(), restAmount)) > 0) {
            order.setAmount(restAmount - Math.min(trade.getTradeVolume(), restAmount));
            return orderRepository.save(order)
                    .doOnNext(savedOrder -> log.info("order 남은 갯수 : " + savedOrder.getAmount()))
                    .flatMap(savedOrder -> insertExecution(trade, order, restAmount));
        }
        return orderRepository.deleteById(order.getId())
                .then(insertExecution(trade, order, restAmount));
    }

    private Mono<Execution> insertExecution(Trade trade, Order order, Double restAmount) {
        return executionRepository.save(
                        Execution.builder()
                                .price(order.getPrice())
                                .userId(order.getUserId())
                                .gubun(order.getGubun())
                                .dateTime(LocalDateTime.now())
                                .code(order.getCode())
                                .amount(Math.min(restAmount, trade.getTradeVolume()))
                                .totalPrice(order.getPrice() * Math.min(restAmount, trade.getTradeVolume()))
                                .sequentialId(trade.getSequentialId())
                                .build()
                )
                .doOnNext(execution -> log.info("execution 갯수 : " + execution.getAmount()))

                //sse 보내기
                .doOnNext(execution -> userChannels.post(execution.getUserId(), executionMessage(execution)));
        //asset에 추가


    }

    private Double calculateAveragePrice(Asset asset, Execution execution) {
        return (asset.getAmount() * asset.getAveragePrice() + execution.getTotalPrice()) / (asset.getAmount() + execution.getAmount());
    }

    private String executionMessage(Execution execution) {
        try {
            return om.writeValueAsString(execution.toSSEResponse());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


//    public void
}
