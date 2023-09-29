package com.coinsimulation.repository;

import com.coinsimulation.entity.Ethereum;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EthereumRepository extends ReactiveMongoRepository<Ethereum, String> {
}
