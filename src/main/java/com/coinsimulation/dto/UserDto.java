package com.coinsimulation.dto;

import com.coinsimulation.entity.Asset;
import com.coinsimulation.entity.Execution;
import com.coinsimulation.entity.Order;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String nickname;
    private String profile;
    private List<Asset> currentAsset;
    private List<Order> orderHistory;
    private List<Execution> executionHistory;
}
