package com.coinsimulation.service;

import com.coinsimulation.entity.OrderHistory;
import com.coinsimulation.repository.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;

    public Flux<OrderHistory> selectOrderHistory(Long userId) {
        return orderHistoryRepository.findTop10ByUserIdOrderByDateTimeDesc(userId);
    }
}
