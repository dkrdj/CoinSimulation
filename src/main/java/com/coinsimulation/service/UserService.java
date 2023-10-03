package com.coinsimulation.service;

import com.coinsimulation.dto.request.UserInfoChangeRequest;
import com.coinsimulation.dto.response.UserResponse;
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
import org.springframework.util.StringUtils;
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


    public Mono<UserResponse> getUserInfo(Long userId) {
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
        return tupleMono.map(tuple -> new UserResponse(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5()));
    }

    public Mono<Void> changeUserInfo(Long userId, UserInfoChangeRequest request) {
        Mono<User> userMono = userRepository.findById(userId);
        return userMono.map(user -> {
                    if (StringUtils.hasText(request.getNickname())) {
                        user.setNickname(request.getNickname());
                    }
                    if (StringUtils.hasText(request.getProfile())) {
                        user.setProfile(request.getProfile());
                    }
                    if (StringUtils.hasText(request.getRole())) {
                        user.setRole(request.getRole());
                    }
                    if (request.getIsNew() != null) {
                        user.setIsNew(request.getIsNew());
                    }
                    return user;
                })
                .flatMap(userRepository::save)
                .then();
    }
}
