package com.coinsimulation.service;

import com.coinsimulation.entity.ExecutionHistory;
import com.coinsimulation.repository.ExecutionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ExecutionHistoryService {
    private final ExecutionHistoryRepository executionHistoryRepository;

    public Flux<ExecutionHistory> selectExecutionHistory(Long userId) {
        return executionHistoryRepository.findByUserId(userId);
    }
}
