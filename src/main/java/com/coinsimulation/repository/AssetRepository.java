package com.coinsimulation.repository;

import com.coinsimulation.entity.Asset;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AssetRepository extends ReactiveCrudRepository<Asset, Long> {
    Flux<Asset> findByUserId(Long userId);
}
