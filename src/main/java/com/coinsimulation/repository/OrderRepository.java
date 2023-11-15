package com.coinsimulation.repository;

import com.coinsimulation.entity.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<Order, Long> {
    Flux<Order> findByUserId(Long userId);

    Mono<Order> deleteByIdAndUserId(Long id, Long userId);
}
