package com.coinsimulation.exception;

public class NotEnoughCashException extends RuntimeException {
    public NotEnoughCashException() {
        super("현금이 부족합니다.");
    }
}
