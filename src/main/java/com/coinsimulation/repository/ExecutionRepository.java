package com.coinsimulation.repository;

import com.coinsimulation.entity.Execution;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ExecutionRepository extends ReactiveMongoRepository<Execution, Long> {
    Flux<Execution> findByUserId(Long userId);
}
