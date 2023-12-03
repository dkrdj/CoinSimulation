package com.coinsimulation.service;

import com.coinsimulation.dto.common.OrderComponent;
import com.coinsimulation.repository.OrderRepository;
import com.coinsimulation.upbit.dto.Trade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Slf4j
@RequiredArgsConstructor
@Service
public class TradeService {
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ExecutionService executionService;
    private Flux<Trade> flux = Flux.empty();

    public Flux<Trade> getFlux() {
        return this.flux;
    }

    public Mono<Void> setFlux(Flux<Trade> tradeFlux) {
        this.flux = tradeFlux
                .map(trade -> {
                    if (trade.getAskBid().equals("ASK")) {
                        executeBuyOrder(trade);
                    } else {
                        executeSellOrder(trade);
                    }
                    return trade;
                })
                .share();
        return Mono.empty();
    }

    private List<Double> getList(List<Double> priceList, Trade trade, String gubun) {
        List<Double> priceRemoveList = new ArrayList<>();
        for (Double price : priceList) {
            List<OrderComponent> orderList = orderService.getList(trade.getCode(), gubun, trade.getTradePrice());
            List<OrderComponent> orderRemoveList = new ArrayList<>();
            for (OrderComponent orderComponent : orderList) {
                orderComponent.setOrderSeq(orderComponent.getOrderSeq() - trade.getTradeVolume());
                if (orderComponent.getOrderSeq() < 0) {
                    //마이너스 된 값만큼 거래 성사 로직 작성
                    Double executionAmount = Math.min(orderComponent.getAmount(), -orderComponent.getOrderSeq());
                    //reactive하게 변경하여 수정해야함
                    executionService.executeTrade(executionAmount, trade, gubun, orderComponent).subscribe();
                    orderComponent.setAmount(orderComponent.getAmount() - executionAmount);
                }
                if (Double.compare(orderComponent.getAmount(), 0d) == 0) {
                    orderRemoveList.add(orderComponent);
                    //reactive하게 변경하여 수정해야함
                    orderRepository.deleteById(orderComponent.getId()).subscribe();
                }
            }
            orderList.removeAll(orderRemoveList);
            if (orderList.size() == 0) {
                priceRemoveList.add(price);
            }
        }
        return priceRemoveList;
    }

    private void executeBuyOrder(Trade trade) {
        String gubun = "buy";
        Queue<Double> orderQueue = orderService.getQueue(trade.getCode(), gubun);
        List<Double> priceList = new ArrayList<>();
        while (!orderQueue.isEmpty()) {
            if (Double.compare(orderQueue.peek(), trade.getTradePrice()) >= 0) {
                priceList.add(orderQueue.poll());
            }
        }
        priceList.remove(getList(priceList, trade, gubun));
        orderQueue.addAll(priceList);
    }

    private void executeSellOrder(Trade trade) {
        String gubun = "sell";
        Queue<Double> orderQueue = orderService.getQueue(trade.getCode(), gubun);
        List<Double> priceList = new ArrayList<>();
        while (!orderQueue.isEmpty()) {
            if (Double.compare(orderQueue.peek(), trade.getTradePrice()) <= 0) {
                priceList.add(orderQueue.poll());
            }
        }
        priceList.remove(getList(priceList, trade, gubun));
        orderQueue.addAll(priceList);
    }

    public Flux<Trade> subscribeOrderBook(String code) {
        return this.flux;
    }
}
