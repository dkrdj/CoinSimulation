package com.coinsimulation.service;

import com.coinsimulation.dto.UserDto;
import com.coinsimulation.entity.Asset;
import com.coinsimulation.entity.Execution;
import com.coinsimulation.entity.Order;
import com.coinsimulation.entity.User;
import com.coinsimulation.repository.AssetRepository;
import com.coinsimulation.repository.ExecutionRepository;
import com.coinsimulation.repository.OrderRepository;
import com.coinsimulation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple5;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final ExecutionRepository executionRepository;
    private final OrderRepository orderRepository;


    public Mono<UserDto> getUserInfo(Long userId) {
        Mono<User> userMono = userRepository.findById(userId);
        Flux<Order> orderFlux = orderRepository.findByUserId(userId);
        Flux<Execution> executionFlux = executionRepository.findByUserId(userId);
        Flux<Asset> assetFlux = assetRepository.findByUserId(userId);
        Mono<Tuple5<String, String, List<Asset>, List<Order>, List<Execution>>>
                tupleMono = Mono.zip(userMono.map(user -> user.getNickname()),
                userMono.map(user -> user.getProfile()),
                assetFlux.collectList(),
                orderFlux.collectList(),
                executionFlux.collectList());
        return tupleMono.map(tuple -> new UserDto(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5()));

    }
}
