package com.coinsimulation.repository;

import com.coinsimulation.entity.Order;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    Flux<Order> findByUserId(Long userId);

    Mono<Order> deleteByIdAndUserId(Long id, Long userId);

    @Query("select orders.* " +
            "from orders " +
            "where orders.gubun = :gubun " +
            "and orders.code = :code " +
            "and orders.price <= :price " +
            "for update"
    )
    Flux<Order> findOrders(String gubun, String code, Double price);


}
