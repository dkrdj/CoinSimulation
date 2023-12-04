package com.coinsimulation.controller;

import com.coinsimulation.dto.request.OrderRequest;
import com.coinsimulation.dto.response.OrderCancelResponse;
import com.coinsimulation.entity.Orders;
import com.coinsimulation.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public Flux<ResponseEntity<Orders>> getOrder(@SessionAttribute("user") Long userId) {
        return orderService.selectOrders(userId).map(ResponseEntity::ok);
    }

    @PostMapping("buy")
    public Mono<ResponseEntity<Orders>> buy(@SessionAttribute("user") Long userId, @RequestBody OrderRequest orderRequest) {
        return orderService.buyOrder(userId, orderRequest).map(ResponseEntity::ok);
    }

    @PostMapping("sell")
    public Mono<ResponseEntity<Orders>> sell(@SessionAttribute("user") Long userId, @RequestBody OrderRequest orderRequest) {
        return orderService.sellOrder(userId, orderRequest).map(ResponseEntity::ok);
    }

    @DeleteMapping("cancel")
    public Mono<ResponseEntity<OrderCancelResponse>> cancel(@SessionAttribute("user") Long userId, Long orderId) {
        return orderService.cancelOrder(userId, orderId).map(ResponseEntity::ok);

    }
}
