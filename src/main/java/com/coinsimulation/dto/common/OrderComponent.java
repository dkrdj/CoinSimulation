package com.coinsimulation.dto.common;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrderComponent {
    private Long id;
    private Long userId;
    private String code;
    private Double price;
    @Setter
    private Double amount;
    @Setter
    private Double orderSeq;
}
