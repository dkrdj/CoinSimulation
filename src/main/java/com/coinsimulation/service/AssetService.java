package com.coinsimulation.service;

import com.coinsimulation.dto.response.AssetResponse;
import com.coinsimulation.exception.OrderExistsException;
import com.coinsimulation.exception.Over10MillionCashException;
import com.coinsimulation.repository.AssetRepository;
import com.coinsimulation.repository.OrderRepository;
import com.coinsimulation.repository.UserRepository;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final TicketService ticketService;
    private final OrderRepository orderRepository;

    public Flux<AssetResponse> getAsset(Long userId) {
        return assetRepository.findByUserId(userId)
                .map(asset -> AssetResponse.builder()
                        .code(asset.getCode())
                        .buyingPrice(asset.getAveragePrice())
                        .amount(asset.getAmount())
                        .currentPrice(ticketService.getCurrentPrice(asset.getCode()) * asset.getAmount())
                        .build());
    }

    public Mono<String> resetCash(Long userId) {
        AtomicDouble totalAsset = new AtomicDouble();
        //코인, 주문 올려둔거 없어야함.
        return orderRepository.findByUserId(userId)
                .flatMap(asset -> Mono.error(OrderExistsException::new))
                .thenMany(assetRepository.findByUserId(userId))
                .reduceWith(
                        () -> Double.valueOf(0d),
                        (totalPrice, asset) -> totalPrice + ticketService.getCurrentPrice(asset.getCode()) * asset.getAmount()
                )
                .doOnNext(totalPrice -> totalAsset.addAndGet(totalPrice))
                .doOnNext(totalPrice -> log.info("asset 합 : " + totalPrice.toString()))
                .then(userRepository.findById(userId))
                .filter(user -> user.getCash() + totalAsset.get() <= 10000000d)
                .switchIfEmpty(Mono.error(Over10MillionCashException::new))
                .doOnNext(user -> user.setCash(30000000d))
                .flatMap(userRepository::save)
                .then(Mono.just("자산 초기화 성공"));
    }
}
