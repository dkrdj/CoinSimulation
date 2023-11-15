package com.coinsimulation.dto.common;


import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class OrderMapKey {
    private String code;
    private String gubun;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderMapKey that = (OrderMapKey) o;
        return code.equals(that.code) && gubun.equals(that.gubun);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, gubun);
    }
}
