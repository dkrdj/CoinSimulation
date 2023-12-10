package com.coinsimulation.controller;

import com.coinsimulation.dto.common.CoinMinValue;
import com.coinsimulation.dto.request.OrderRequest;
import com.coinsimulation.dto.response.OrderResponse;
import com.coinsimulation.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Flux<OrderResponse>> getOrder(@SessionAttribute("user") Long userId) {
        return ResponseEntity.ok(orderService.selectOrders(userId));
    }

    @PostMapping("buy")
    public Mono<ResponseEntity<?>> buy(@SessionAttribute("user") Long userId, @RequestBody OrderRequest orderRequest) {
        if (orderRequest.getPrice() % CoinMinValue.BITCOIN != 0) {
            return Mono.just(ResponseEntity.badRequest().body("최소 금액 (" + CoinMinValue.BITCOIN + ") 단위로 주문 신청을 해야 합니다."));
        }
        return orderService.buyOrder(userId, orderRequest).map(ResponseEntity::ok);
    }

    @PostMapping("sell")
    public Mono<ResponseEntity<?>> sell(@SessionAttribute("user") Long userId, @RequestBody OrderRequest orderRequest) {
        if (orderRequest.getPrice() % CoinMinValue.BITCOIN != 0) {
            return Mono.just(ResponseEntity.badRequest().body("최소 금액 (" + CoinMinValue.BITCOIN + ") 단위로 주문 신청을 해야 합니다."));
        }
        return orderService.sellOrder(userId, orderRequest).map(ResponseEntity::ok);
    }

    @DeleteMapping("{orderId}")
    public Mono<ResponseEntity<Void>> cancel(@SessionAttribute("user") Long userId, @PathVariable Long orderId) {
        return orderService.cancelOrder(userId, orderId).map(ResponseEntity::ok);
    }
}

