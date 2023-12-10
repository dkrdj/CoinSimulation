package com.coinsimulation.dto.response;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderResponse {
    private Long id;
    private String code;
    private String gubun;
    private Double price;
    private Double amount;
    private Timestamp dateTime;
}
