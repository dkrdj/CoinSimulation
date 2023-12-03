package com.coinsimulation.service;

import com.coinsimulation.dto.common.OrderComponent;
import com.coinsimulation.dto.common.OrderMapKey;
import com.coinsimulation.dto.request.OrderRequest;
import com.coinsimulation.dto.response.OrderCancelResponse;
import com.coinsimulation.entity.Order;
import com.coinsimulation.repository.OrderRepository;
import com.coinsimulation.upbit.dto.OrderBookUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.utils.StringUtils;

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
    private final OrderRepository orderRepository;
    private final OrderBookService orderBookService;
    public Map<OrderMapKey, Map<Double, List<OrderComponent>>> orderList = new ConcurrentHashMap<>();
    public Map<OrderMapKey, PriorityBlockingQueue<Double>> orderQueue = new ConcurrentHashMap<>();

    public Flux<Order> selectOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public PriorityBlockingQueue<Double> getQueue(String code, String gubun) {
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

    public Mono<Order> buyOrder(Long userId, OrderRequest orderRequest) {
        Order order = getBuilder(userId, orderRequest)
                .gubun("buy").build();
        return this.orderRepository.save(order)
                .doOnNext(putOrderInList())
                .doOnNext(putPriceInQueue());
    }

    public Mono<Order> sellOrder(Long userId, OrderRequest orderRequest) {
        Order order = getBuilder(userId, orderRequest)
                .gubun("sell").build();
        return this.orderRepository.save(order)
                .doOnNext(putOrderInList())
                .doOnNext(putPriceInQueue());
    }

    public Mono<OrderCancelResponse> cancelOrder(Long userId, Long orderId) {
        return this.orderRepository.deleteByIdAndUserId(orderId, userId)
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

    private Double getBuySequence(String code, Double price) {
        //bid_price
        for (OrderBookUnit orderBookUnit : this.orderBookService.orderBookUnitList) {
            if (Double.compare(price, orderBookUnit.getBidPrice()) == 0) {
                return orderBookUnit.getBidSize();
            }
        }
        return 0d;
    }

    private Double getSellSequence(String code, Double price) {
        //bid_price
        for (OrderBookUnit orderBookUnit : this.orderBookService.orderBookUnitList) {
            if (Double.compare(price, orderBookUnit.getAskPrice()) == 0) {
                return orderBookUnit.getAskSize();
            }
        }
        return 0d;
    }

    private Consumer<Order> putOrderInList() {
        return (savedOrder) -> getList(savedOrder.getCode(), savedOrder.getGubun(), savedOrder.getPrice())
                .add(OrderComponent.builder()
                        .id(savedOrder.getId())
                        .userId(savedOrder.getUserId())
                        .code(savedOrder.getCode())
                        .price(savedOrder.getPrice())
                        .amount(savedOrder.getAmount())
                        .orderSeq(StringUtils.equals(savedOrder.getGubun(), "buy")
                                ? getBuySequence(savedOrder.getCode(), savedOrder.getPrice())
                                : getSellSequence(savedOrder.getCode(), savedOrder.getPrice())
                        )
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
}
