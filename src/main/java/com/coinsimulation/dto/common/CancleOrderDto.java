package com.coinsimulation.dto.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancleOrderDto {
    private String code;
    private Double amount;
}
