package com.coinsimulation.repository;

import com.coinsimulation.entity.OrderHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrderHistoryRepository extends ReactiveCrudRepository<OrderHistory, Long> {
    Flux<OrderHistory> findByUserId(Long userId);
}
