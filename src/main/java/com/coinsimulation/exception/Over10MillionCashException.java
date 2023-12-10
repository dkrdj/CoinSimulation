package com.coinsimulation.exception;

public class Over10MillionCashException extends RuntimeException {
    public Over10MillionCashException() {
        super("현재 자선(코인+현금)이 천만 원 이상 존재합니다.");
    }
}
