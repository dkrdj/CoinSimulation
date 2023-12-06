package com.coinsimulation.entity;

import com.coinsimulation.dto.response.ExecutionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Builder
public class Execution {
    private Long userId;
    private String gubun;
    private Double amount;
    private Double price;
    private Double KRW;
    private LocalDateTime dateTime;
    private Long sequentialId;

    public ExecutionResponse toResponse() {
        ExecutionResponse executionResponse = new ExecutionResponse();
        BeanUtils.copyProperties(this, executionResponse);
        return executionResponse;
    }
}
