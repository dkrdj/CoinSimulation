package com.coinsimulation.repository;

import com.coinsimulation.entity.ExecutionHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ExecutionHistoryRepository extends ReactiveCrudRepository<ExecutionHistory, Long> {

    Flux<ExecutionHistory> findByUserId(Long userId);
}
