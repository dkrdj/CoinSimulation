package com.coinsimulation.entity;

import com.coinsimulation.dto.response.ExecutionResponse;
import com.coinsimulation.dto.response.ExecutionSSEResponse;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Builder
@ToString
public class Execution {
    @Id
    private Long id;
    private Long userId;
    private String gubun;
    private Double amount;
    private String code;
    private Double price;
    private Double totalPrice;
    private LocalDateTime dateTime;
    private Long sequentialId;

    public ExecutionResponse toResponse() {
        ExecutionResponse executionResponse = new ExecutionResponse();
        BeanUtils.copyProperties(this, executionResponse);
        return executionResponse;
    }

    public ExecutionSSEResponse toSSEResponse() {
        ExecutionSSEResponse sseResponse = new ExecutionSSEResponse();
        BeanUtils.copyProperties(this, sseResponse);
        return sseResponse;
    }
}
