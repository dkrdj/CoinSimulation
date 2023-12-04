package com.coinsimulation.service;

import com.coinsimulation.dto.common.OrderComponent;
import com.coinsimulation.dto.common.OrderMapKey;
import com.coinsimulation.dto.request.OrderRequest;
import com.coinsimulation.dto.response.OrderCancelResponse;
import com.coinsimulation.entity.Orders;
import com.coinsimulation.repository.OrderRepository;
import com.coinsimulation.upbit.dto.OrderBookUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderBookService orderBookService;
    public Map<OrderMapKey, Map<Double, List<OrderComponent>>> orderList = new ConcurrentHashMap<>();
    public Map<OrderMapKey, PriorityBlockingQueue<Double>> orderQueue = new ConcurrentHashMap<>();

    public Flux<Orders> selectOrders(Long userId) {
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

    public Mono<Orders> buyOrder(Long userId, OrderRequest orderRequest) {
        Orders orders = getBuilder(userId, orderRequest)
                .gubun("buy")
                .build();
        log.error(orders.toString());
        return this.orderRepository.save(orders)
                .doOnNext(putOrderInList())
                .doOnNext(putPriceInQueue());
    }

    public Mono<Orders> sellOrder(Long userId, OrderRequest orderRequest) {
        Orders orders = getBuilder(userId, orderRequest)
                .gubun("sell").build();
        return this.orderRepository.save(orders)
                .doOnNext(putOrderInList())
                .doOnNext(putPriceInQueue());
    }

    public Mono<OrderCancelResponse> cancelOrder(Long userId, Long orderId) {
        return this.orderRepository.deleteByIdAndUserId(orderId, userId)
                .doOnNext(orders ->
                        getList(orders.getCode(), orders.getGubun(), orders.getPrice())
                                .remove(orders)
                ).map(orders -> OrderCancelResponse.builder()
                        .id(orders.getId())
                        .userId(orders.getUserId())
                        .code(orders.getCode())
                        .gubun(orders.getGubun())
                        .build());
    }

    private Consumer<Orders> putPriceInQueue() {
        return (savedOrders) -> getQueue(savedOrders.getCode(), savedOrders.getGubun())
                .offer(savedOrders.getPrice());
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

    private Consumer<Orders> putOrderInList() {
        return (savedOrders) -> getList(savedOrders.getCode(), savedOrders.getGubun(), savedOrders.getPrice())
                .add(OrderComponent.builder()
                        .id(savedOrders.getId())
                        .userId(savedOrders.getUserId())
                        .code(savedOrders.getCode())
                        .price(savedOrders.getPrice())
                        .amount(savedOrders.getAmount())
                        .orderSeq(StringUtils.equals(savedOrders.getGubun(), "buy")
                                ? getBuySequence(savedOrders.getCode(), savedOrders.getPrice())
                                : getSellSequence(savedOrders.getCode(), savedOrders.getPrice())
                        )
                        .build());
    }

    private Orders.OrdersBuilder getBuilder(Long userId, OrderRequest orderRequest) {
        return Orders.builder()
                .userId(userId)
                .code(orderRequest.getCode())
                .price(orderRequest.getPrice())
                .amount(orderRequest.getAmount());
//                .dateTime(LocalDateTime.now());
    }
}
