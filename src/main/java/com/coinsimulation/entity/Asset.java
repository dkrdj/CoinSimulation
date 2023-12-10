package com.coinsimulation.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table
@Builder
@ToString
public class Asset {
    @Id
    private Long id;
    private Long userId;
    private String code;
    @Setter
    private Double averagePrice;
    @Setter
    private Double amount;
}
