package com.coinsimulation.exception;

public class OrderExistsException extends RuntimeException {
    public OrderExistsException() {
        super("체결되지 않은 주문이 있습니다.");
    }
}
