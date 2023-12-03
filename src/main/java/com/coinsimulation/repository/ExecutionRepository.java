package com.coinsimulation.repository;

import com.coinsimulation.entity.Execution;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ExecutionRepository extends ReactiveCrudRepository<Execution, Long> {
    Flux<Execution> findByUserId(Long userId);
}
