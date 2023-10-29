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
import com.coinsimulation.util.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple5;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final ExecutionRepository executionRepository;
    private final OrderRepository orderRepository;
    private final S3Utils s3Utils;

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

    public Mono<User> changeUserInfo(Long userId, UserInfoChangeRequest request) {
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
                    if (Objects.nonNull(request.getIsNew())) {
                        user.setIsNew(request.getIsNew());
                    }
                    return user;
                })
                .flatMap(userRepository::save);
    }

    public Mono<User> changeUserProfile(FilePart filePart, Long userId) {
        return s3Utils.uploadObject(filePart, userId)
                .map(fileResponse -> fileResponse.path())
                .flatMap(path -> userRepository.findById(userId).map(user -> {
                    user.setProfile(path);
                    return user;
                }));
    }
}
