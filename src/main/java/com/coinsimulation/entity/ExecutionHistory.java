package com.coinsimulation.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table
public class ExecutionHistory {
    @Id
    private Long id;
    private Long userId;
    private String gubun;
    private Double amount;
    private Double price;
    private Double KRW;
    private LocalDateTime dateTime;
}
