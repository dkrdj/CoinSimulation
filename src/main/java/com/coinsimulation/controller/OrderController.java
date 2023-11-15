package com.coinsimulation.controller;

import com.coinsimulation.dto.request.OrderRequest;
import com.coinsimulation.dto.response.OrderCancelResponse;
import com.coinsimulation.entity.Order;
import com.coinsimulation.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("buy")
    public Mono<ResponseEntity<Order>> buy(@SessionAttribute("user") Long userId, @RequestBody OrderRequest orderRequest) {
        return orderService.buyOrder(userId, orderRequest).map(ResponseEntity::ok);
    }

    @PostMapping("sell")
    public Mono<ResponseEntity<Order>> sell(@SessionAttribute("user") Long userId, @RequestBody OrderRequest orderRequest) {
        return orderService.sellOrder(userId, orderRequest).map(ResponseEntity::ok);
    }

    @PutMapping("cancel")
    public Mono<ResponseEntity<OrderCancelResponse>> cancel(@SessionAttribute("user") Long userId, Long orderId) {
        return orderService.cancelOrder(userId, orderId).map(ResponseEntity::ok);

    }
}
