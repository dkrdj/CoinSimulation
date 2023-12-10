package com.coinsimulation.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionResponse {
    private Long id;
    private String gubun;
    private Double amount;
    private Double price;
    private Double totalPrice;
    private LocalDateTime dateTime;
}
