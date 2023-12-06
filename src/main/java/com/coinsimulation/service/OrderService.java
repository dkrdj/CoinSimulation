package com.coinsimulation.service;

import com.coinsimulation.dto.request.OrderRequest;
import com.coinsimulation.dto.response.OrderResponse;
import com.coinsimulation.entity.Order;
import com.coinsimulation.exception.NoOrderException;
import com.coinsimulation.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;

    public Flux<OrderResponse> selectOrders(Long userId) {
        return orderRepository.findByUserId(userId)
                .doOnNext(order -> log.info("Order : " + order.toString()))
                .map(order -> order.toResponse())
                .doOnNext(order -> log.info("OrderResponse : " + order.toString()));
    }

    public Mono<OrderResponse> buyOrder(Long userId, OrderRequest orderRequest) {
        return this.orderRepository.save(
                        Order.builder()
                                .userId(userId)
                                .code(orderRequest.getCode())
                                .price(orderRequest.getPrice())
                                .amount(orderRequest.getAmount())
                                .gubun("buy")
                                .dateTime(Timestamp.valueOf(LocalDateTime.now()))
                                .build()
                )
                .doOnNext(order -> log.info("Order : " + order.toString()))
                .map(order -> order.toResponse())
                .doOnNext(order -> log.info("OrderResponse : " + order.toString()));
    }

    public Mono<OrderResponse> sellOrder(Long userId, OrderRequest orderRequest) {
        return this.orderRepository.save(
                        Order.builder()
                                .userId(userId)
                                .code(orderRequest.getCode())
                                .price(orderRequest.getPrice())
                                .amount(orderRequest.getAmount())
                                .gubun("sell")
                                .build()
                )
                .doOnNext(order -> log.info("Order : " + order.toString()))
                .map(order -> order.toResponse())
                .doOnNext(order -> log.info("OrderResponse : " + order.toString()));
    }

    public Mono<Void> cancelOrder(Long userId, Long orderId) {
        return this.orderRepository.deleteByIdAndUserId(orderId, userId)
                //값이 없으면 일치하는 주문이 없는 것 -> 예외 발생
                .switchIfEmpty(Mono.error(new NoOrderException()))
                .then();
    }
}
