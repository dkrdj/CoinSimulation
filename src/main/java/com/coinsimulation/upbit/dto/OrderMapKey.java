package com.coinsimulation.upbit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderMapKey {
    private String code;
    private String gubun;
    private Double price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderMapKey that = (OrderMapKey) o;
        return Objects.equals(code, that.code) && Objects.equals(gubun, that.gubun) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, gubun, price);
    }
}
