package com.coinsimulation.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Asset {
    @Id
    private Long id;
    private Long userId;
    private String gubun;
    private Double baseAsset;
    private Double increasedAsset;
    private Double amount;
}
