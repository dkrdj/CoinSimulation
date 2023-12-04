package com.coinsimulation.repository;

import com.coinsimulation.entity.Orders;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Orders, Long> {
    Flux<Orders> findByUserId(Long userId);

    Mono<Orders> deleteByIdAndUserId(Long id, Long userId);
}
