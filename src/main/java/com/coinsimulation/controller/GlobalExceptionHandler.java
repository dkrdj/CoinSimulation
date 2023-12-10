package com.coinsimulation.controller;

import com.coinsimulation.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FileValidatorException.class)
    public ErrorResponse handleFileValidatorException(FileValidatorException e) {
        return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, e.getMessage())
                .title("FileValidatorException")
                .property("timestamp", Instant.now())
                .build();
    }

    @ExceptionHandler(DataBufferLimitException.class)
    public ErrorResponse handleDataBufferLimitException(DataBufferLimitException e) {
        return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, e.getMessage())
                .title("DataBufferLimitException")
                .property("timestamp", Instant.now())
                .build();
    }

    @ExceptionHandler(OrderExistsException.class)
    public ErrorResponse handleOrderExistsException(OrderExistsException e) {
        return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, e.getMessage())
                .title("OrderExistsException")
                .property("timestamp", Instant.now())
                .build();
    }

    @ExceptionHandler(Over10MillionCashException.class)
    public ErrorResponse handleOver10MillionCashException(Over10MillionCashException e) {
        return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, e.getMessage())
                .title("Over10MillionCashException")
                .property("timestamp", Instant.now())
                .build();
    }

    @ExceptionHandler(NoOrderException.class)
    public ErrorResponse handleNoOrderException(NoOrderException e) {
        return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, e.getMessage())
                .title("NoOrderException")
                .property("timestamp", Instant.now())
                .build();
    }

    @ExceptionHandler(NotEnoughCoinException.class)
    public ErrorResponse handleNotEnoughCoinException(NotEnoughCoinException e) {
        return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, e.getMessage())
                .title("NotEnoughCoinException")
                .property("timestamp", Instant.now())
                .build();
    }

    @ExceptionHandler(NotEnoughCashException.class)
    public ErrorResponse handleNotEnoughCashException(NotEnoughCashException e) {
        return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, e.getMessage())
                .title("NotEnoughCashException")
                .property("timestamp", Instant.now())
                .build();
    }


}