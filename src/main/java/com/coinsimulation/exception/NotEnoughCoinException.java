package com.coinsimulation.exception;

public class NotEnoughCoinException extends RuntimeException {
    public NotEnoughCoinException() {
        super("코인이 부족합니다.");
    }
}
