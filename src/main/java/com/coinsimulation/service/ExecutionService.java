package com.coinsimulation.service;

import com.coinsimulation.dto.response.ExecutionResponse;
import com.coinsimulation.entity.Execution;
import com.coinsimulation.entity.Order;
import com.coinsimulation.repository.ExecutionRepository;
import com.coinsimulation.repository.OrderRepository;
import com.coinsimulation.upbit.dto.Trade;
import com.google.common.util.concurrent.AtomicDouble;
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
    private final ExecutionRepository executionRepository;
    private final OrderRepository orderRepository;
    private AtomicDouble doubleNum = new AtomicDouble(0d);

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
        return orderRepository.findOrders(gubun, trade.getCode(), trade.getTradePrice())
//                .doOnNext(order -> log.info(order.toString()))
                .transform(orderFlux -> orderFlux
                        .flatMap(order -> updateOrder(trade, order))
                )
                //순서를 마이너스 하는 로직 필요
//                .flatMap(order -> updateOrder(trade, order))
//                .retryWhen(Retry.max(1).filter(OptimisticLockingFailureException.class::isInstance))
//                .flatMap(order ->
//                        Mono.defer(() -> updateOrder(trade, order))
//                                .onErrorResume(
//                                        throwable -> {
//                                            log.info("version 문제로 재시도");
//                                            return Mono.defer(() -> orderRepository.findById(order.getId())
//                                                    .flatMap(newOrder ->
//                                                            Mono.defer(() -> updateOrder(trade, newOrder))));
//                                        }
//                                )
//                )
                .then(Mono.just(trade));

        //체결 진행
//                        executionRepository.save(
//                                        Execution.builder()
//                                                .price(order.getPrice())
//                                                .userId(order.getUserId())
//                                                .gubun(order.getGubun())
//                                                .dateTime(LocalDateTime.now())
//                                                .amount(Math.min(order.getAmount(), trade.getTradeVolume()))
//                                                .KRW(order.getPrice() * Math.min(order.getAmount(), trade.getTradeVolume()))
//                                                .sequentialId(trade.getSequentialId())
//                                                .build()
//                                )
//                                .doOnNext(execution -> log.info("execution 갯수 : " + execution.getAmount()))
//                                //sse로 보내야함
//                                //체결 수량만큼 order 수량에서 까야함
//                                .flatMap(execution -> {
//                                    if (Double.compare(order.getAmount(), execution.getAmount()) > 0) {
//                                        order.setAmount(order.getAmount() - execution.getAmount());
//                                        return orderRepository.save(order).onErrorContinue((throwable, o) -> {
//                                            if(throwable instanceof OptimisticLockingFailureException){
//                                                return
//                                            }
//                                        }).doOnNext(order1 -> log.info("order 남은 갯수" + order1.getAmount()));
//                                    }
//                                    return orderRepository.deleteById(order.getId());
//                                })
//                )
//                .then(Mono.just(trade));
    }

    private Mono<Execution> updateOrder(Trade trade, Order order) {
        Double restAmount = order.getAmount();
        if (Double.compare(restAmount, Math.min(trade.getTradeVolume(), restAmount)) > 0) {
            order.setAmount(restAmount - Math.min(trade.getTradeVolume(), restAmount));
            return orderRepository.save(order)
                    .doOnNext(savedOrder -> log.info("order 남은 갯수 : " + savedOrder.getAmount()))
                    .flatMap(savedOrder -> insertExecution(trade, order, restAmount));
//                    .then(Mono.empty());
        }
        return orderRepository.deleteById(order.getId())
                .then(
                        insertExecution(trade, order, restAmount).then(Mono.empty())
                );

    }

    private Mono<Execution> insertExecution(Trade trade, Order order, Double restAmount) {
        return executionRepository.save(
                        Execution.builder()
                                .price(order.getPrice())
                                .userId(order.getUserId())
                                .gubun(order.getGubun())
                                .dateTime(LocalDateTime.now())
                                .amount(Math.min(restAmount, trade.getTradeVolume()))
                                .KRW(order.getPrice() * Math.min(order.getAmount(), trade.getTradeVolume()))
                                .sequentialId(trade.getSequentialId())
                                .build()
                )
                .doOnNext(execution -> log.info("execution 갯수 : " + execution.getAmount()));
        //sse로 보내야함
        //체결 수량만큼 order 수량에서 까야함
    }


//    public void
}
