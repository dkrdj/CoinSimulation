package com.coinsimulation.service;

import com.coinsimulation.dto.request.OrderRequest;
import com.coinsimulation.dto.response.OrderResponse;
import com.coinsimulation.entity.Asset;
import com.coinsimulation.entity.Order;
import com.coinsimulation.entity.User;
import com.coinsimulation.exception.NoOrderException;
import com.coinsimulation.exception.NotEnoughCashException;
import com.coinsimulation.exception.NotEnoughCoinException;
import com.coinsimulation.repository.AssetRepository;
import com.coinsimulation.repository.OrderRepository;
import com.coinsimulation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;

    public Flux<OrderResponse> selectOrders(Long userId) {
        return orderRepository.findByUserId(userId)
                .doOnNext(order -> log.info("Order : " + order.toString()))
                .map(order -> order.toResponse())
                .doOnNext(order -> log.info("OrderResponse : " + order.toString()));
    }

    @Transactional
    public Mono<OrderResponse> buyOrder(Long userId, OrderRequest orderRequest) {
        return this.userRepository.findByIdForUpdate(userId)
                .handle((user, sink) -> {
                    Double totalPrice = orderRequest.getPrice() * orderRequest.getAmount();
                    if (user.getCash() >= totalPrice) {
                        user.setCash(user.getCash() - totalPrice);
                        sink.next(user);
                    } else {
                        sink.error(new NotEnoughCashException());
                    }
                })
                .cast(User.class)
                .flatMap(userRepository::save)
                .then(this.orderRepository.save(
                                Order.builder()
                                        .userId(userId)
                                        .code(orderRequest.getCode())
                                        .price(orderRequest.getPrice())
                                        .amount(orderRequest.getAmount())
                                        .gubun("buy")
                                        .dateTime(LocalDateTime.now())
                                        .build()
                        )
                        .doOnNext(order -> log.info("Order : " + order.toString()))
                        .map(order -> order.toResponse())
                        .doOnNext(order -> log.info("OrderResponse : " + order.toString())));
    }

    @Transactional
    public Mono<OrderResponse> sellOrder(Long userId, OrderRequest orderRequest) {
        return this.assetRepository.findByUserIdAndCodeForUpdate(userId, orderRequest.getCode())
                .switchIfEmpty(Mono.error(NotEnoughCoinException::new))
                .handle((asset, sink) -> {
                    if (asset.getAmount() >= orderRequest.getAmount()) {
                        asset.setAmount(asset.getAmount() - orderRequest.getAmount());
                        sink.next(asset);
                    } else {
                        sink.error(new NotEnoughCoinException());
                    }
                })
                .cast(Asset.class)
                .flatMap(assetRepository::save)
                .flatMap(asset -> this.orderRepository.save(
                                Order.builder()
                                        .userId(userId)
                                        .code(orderRequest.getCode())
                                        .price(orderRequest.getPrice())
                                        .amount(orderRequest.getAmount())
                                        .gubun("sell")
                                        .dateTime(LocalDateTime.now())
                                        .prePrice(asset.getAveragePrice())
                                        .build()
                        )
                        .doOnNext(order -> log.info("Order : " + order.toString()))
                        .map(order -> order.toResponse())
                        .doOnNext(order -> log.info("OrderResponse : " + order.toString())));
    }

    @Transactional
    public Mono<Void> cancelOrder(Long userId, Long orderId) {
        return this.orderRepository.findByIdAndUserIdForUpdate(orderId, userId)
                //값이 없으면 일치하는 주문이 없는 것 -> 예외 발생
                .switchIfEmpty(Mono.error(new NoOrderException()))
                .flatMap(order -> {
                    if (order.getGubun().equals("buy")) {
                        return userRepository.findByIdForUpdate(userId)
                                .doOnNext(user -> user.setCash(user.getCash() + order.getAmount() * order.getPrice()))
                                .flatMap(userRepository::save);
                    }
                    return assetRepository.findByUserIdAndCodeForUpdate(userId, order.getCode())
                            .doOnNext(asset -> {
                                asset.setAveragePrice(calculateAveragePrice(asset, order));
                                asset.setAmount(asset.getAmount() + order.getAmount());
                            })
                            .flatMap(assetRepository::save);
                })
                .then();
    }

    private Double calculateAveragePrice(Asset asset, Order order) {
        return (asset.getAveragePrice() * asset.getAmount() + order.getPrePrice() * order.getAmount()) / (asset.getAmount() + order.getAmount());
    }
}
