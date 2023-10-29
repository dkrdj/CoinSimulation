package com.coinsimulation.upbit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TickerData {
    private String type;
    private List<String> codes;
    @JsonProperty("isOnlySnapshot")
    private boolean isOnlySnapshot;
    @JsonProperty("isOnlyRealtime")
    private boolean isOnlyRealtime;
}
