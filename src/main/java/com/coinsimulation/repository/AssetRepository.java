package com.coinsimulation.repository;

import com.coinsimulation.entity.Asset;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AssetRepository extends ReactiveMongoRepository<Asset, Long> {
    Flux<Asset> findByUserId(Long userId);
}
