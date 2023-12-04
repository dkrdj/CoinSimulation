package com.coinsimulation.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Builder
@ToString
public class Orders {
    @Id
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    private String code;
    private String gubun;
    private Double price;
    private Double amount;
    @JsonProperty("date_time")
    private LocalDateTime dateTime;
}
