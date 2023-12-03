package com.coinsimulation.service;

import com.coinsimulation.dto.common.OrderComponent;
import com.coinsimulation.entity.Execution;
import com.coinsimulation.repository.ExecutionRepository;
import com.coinsimulation.upbit.dto.Trade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutionService {
    private final ExecutionRepository executionRepository;

    public Flux<Execution> selectExecution(Long userId) {
        return executionRepository.findByUserId(userId);
    }

    public Mono<Execution> executeTrade(Double executionAmount, Trade trade, String gubun, OrderComponent orderComponent) {
        return executionRepository.save(Execution.builder()
                .amount(executionAmount)
                .KRW(executionAmount * trade.getTradePrice())
                .dateTime(LocalDateTime.now())
                .gubun(gubun)
                .userId(orderComponent.getUserId())
                .price(orderComponent.getPrice())
                .build());
    }


//    public void
}
