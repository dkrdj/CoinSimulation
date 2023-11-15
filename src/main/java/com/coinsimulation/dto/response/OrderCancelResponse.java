package com.coinsimulation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancelResponse {
    private Long id;
    private Long userId;
    private String code;
    private String gubun;
}
