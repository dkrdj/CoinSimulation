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
public class Order {
    @Id
    private Long id;
    private Long userId;
    private String code;
    private String gubun;
    private Double price;
    private Double amount;
    private Double preAmount;
    private LocalDateTime dateTime;
}