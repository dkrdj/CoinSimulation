package com.coinsimulation.dto.common;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderBookAtomicUnit {
    private AtomicDouble askPrice;
    private AtomicDouble askSize;
    private AtomicDouble bidPrice;
    private AtomicDouble bidSize;
}
