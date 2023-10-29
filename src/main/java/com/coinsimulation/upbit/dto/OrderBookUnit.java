package com.coinsimulation.upbit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookUnit {
    private Double askPrice;
    private Double bidPrice;
    private Double askSize;
    private Double bidSize;
}
