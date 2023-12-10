package com.coinsimulation.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AssetResponse {
    private String code;
    private Double amount;
    private Double buyingPrice;
    private Double currentPrice;
}
