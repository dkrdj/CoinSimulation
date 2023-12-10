package com.coinsimulation.exception;

public class NoOrderException extends RuntimeException {
    public NoOrderException() {
        super("취소할 주문이 없습니다.");
    }
}
