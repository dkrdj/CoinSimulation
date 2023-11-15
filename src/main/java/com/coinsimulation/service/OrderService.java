package com.coinsimulation.service;

import com.coinsimulation.dto.common.OrderComponent;
import com.coinsimulation.dto.common.OrderMapKey;
import com.coinsimulation.dto.request.OrderRequest;
import com.coinsimulation.dto.response.OrderCancelResponse;
import com.coinsimulation.entity.Order;
import com.coinsimulation.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class OrderService {
    //    private AtomicDouble priceBuy;
//    private AtomicDouble priceBuyVolume;
//    private AtomicDouble priceSell;
//    private AtomicDouble priceSellVolume;
    private final OrderRepository orderRepository;
    private Map<OrderMapKey, Map<Double, List<OrderComponent>>> orderList = new ConcurrentHashMap<>();
    private Map<OrderMapKey, PriorityBlockingQueue<Double>> orderQueue = new ConcurrentHashMap<>();

    public Mono<Order> buyOrder(Long userId, OrderRequest orderRequest) {
        Order order = getBuilder(userId, orderRequest)
                .gubun("buy").build();
        return orderRepository.save(order)
                .doOnNext(putOrderInList())
                .doOnNext(putPriceInQueue());
    }


    public Mono<Order> sellOrder(Long userId, OrderRequest orderRequest) {
        Order order = getBuilder(userId, orderRequest)
                .gubun("sell").build();
        return orderRepository.save(order)
                .doOnNext(putOrderInList())
                .doOnNext(putPriceInQueue());
    }

    public Mono<OrderCancelResponse> cancelOrder(Long userId, Long orderId) {
        return orderRepository.deleteByIdAndUserId(orderId, userId)
                .doOnNext(order ->
                        getList(order.getCode(), order.getGubun(), order.getPrice())
                                .remove(order)
                ).map(order -> OrderCancelResponse.builder()
                        .id(order.getId())
                        .userId(order.getUserId())
                        .code(order.getCode())
                        .gubun(order.getGubun())
                        .build());
    }

    private Consumer<Order> putPriceInQueue() {
        return (savedOrder) -> getQueue(savedOrder.getCode(), savedOrder.getGubun())
                .offer(savedOrder.getPrice());
    }

    private Consumer<Order> putOrderInList() {
        return (savedOrder) -> getList(savedOrder.getCode(), savedOrder.getGubun(), savedOrder.getPrice())
                .add(OrderComponent.builder()
                        .id(savedOrder.getId())
                        .userId(savedOrder.getUserId())
                        .code(savedOrder.getCode())
                        .price(savedOrder.getPrice())
                        .amount(savedOrder.getAmount())
                        .build());
    }

    private Order.OrderBuilder getBuilder(Long userId, OrderRequest orderRequest) {
        return Order.builder()
                .userId(userId)
                .code(orderRequest.getCode())
                .price(orderRequest.getPrice())
                .amount(orderRequest.getAmount())
                .preAmount(orderRequest.getPreAmount())
                .dateTime(LocalDateTime.now());
    }

    private PriorityBlockingQueue<Double> getQueue(String code, String gubun) {
        return this.orderQueue.computeIfAbsent(new OrderMapKey(code, gubun), o -> {
            if (gubun.equals("buy")) {
                return new PriorityBlockingQueue<>();
            }
            return new PriorityBlockingQueue<>(10, ((o1, o2) -> Double.compare(o2, o1)));
        });
    }

    public List<OrderComponent> getList(String code, String gubun, Double price) {
        return this.orderList.computeIfAbsent(new OrderMapKey(code, gubun), s -> new ConcurrentHashMap<>())
                .computeIfAbsent(price, p -> new CopyOnWriteArrayList<>());
    }
}
