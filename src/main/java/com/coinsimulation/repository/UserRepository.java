package com.coinsimulation.repository;

import com.coinsimulation.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByProviderId(Long providerId);

    @Query("select users.* " +
            "from users " +
            "where users.id = :id " +
            "for update")
    Mono<User> findByIdForUpdate(Long id);
}
