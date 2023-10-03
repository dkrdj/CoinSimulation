package com.coinsimulation.repository;

import com.coinsimulation.entity.coin.Bitcoin;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BitcoinRepository extends ReactiveMongoRepository<Bitcoin, String> {
}
