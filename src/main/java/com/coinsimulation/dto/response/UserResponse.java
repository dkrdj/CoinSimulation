package com.coinsimulation.dto.response;

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
public class UserResponse {
    private String nickname;
    private String profile;
    private List<Asset> currentAsset;
    private List<Order> orderHistory;
    private List<Execution> executionHistory;
}
