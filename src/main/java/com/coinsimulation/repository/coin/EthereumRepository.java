package com.coinsimulation.repository.coin;

import com.coinsimulation.entity.coin.Ethereum;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EthereumRepository extends ReactiveMongoRepository<Ethereum, String> {
}
