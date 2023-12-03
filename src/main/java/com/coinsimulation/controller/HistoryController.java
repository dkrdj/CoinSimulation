package com.coinsimulation.controller;

import com.coinsimulation.entity.ExecutionHistory;
import com.coinsimulation.entity.OrderHistory;
import com.coinsimulation.service.ExecutionHistoryService;
import com.coinsimulation.service.OrderHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("history")
public class HistoryController {
    private final OrderHistoryService orderHistoryService;
    private final ExecutionHistoryService executionHistoryService;

    @GetMapping("order")
    public Flux<ResponseEntity<OrderHistory>> getOrders(@SessionAttribute("user") Long userId) {
        return orderHistoryService.selectOrderHistory(userId).map(ResponseEntity::ok);
    }

    @GetMapping("execution")
    public Flux<ResponseEntity<ExecutionHistory>> getExecutions(@SessionAttribute("user") Long userId) {
        return executionHistoryService.selectExecutionHistory(userId).map(ResponseEntity::ok);
    }
}
