package com.coinsimulation.repository;

import com.coinsimulation.entity.Asset;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AssetRepository extends ReactiveCrudRepository<Asset, Long> {
    Flux<Asset> findByUserId(Long userId);

    @Query("select asset.* " +
            "from asset " +
            "where asset.user_id = :userId " +
            "and asset.code = :code " +
            "for update")
    Mono<Asset> findByUserIdAndCodeForUpdate(Long userId, String code);
}
