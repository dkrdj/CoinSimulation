package com.coinsimulation.repository;

import com.coinsimulation.entity.Star;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StarRepository extends ReactiveCrudRepository<Star, Long> {
    Flux<Star> findByUserId(Long userId);

    Mono<Void> deleteByUserIdAndCode(Long userId, String code);
}
