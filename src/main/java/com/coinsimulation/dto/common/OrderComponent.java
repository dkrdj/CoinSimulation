package com.coinsimulation.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrderComponent {
    private Long id;
    private Long userId;
    private String code;
    private Double price;
    private Double amount;
}
