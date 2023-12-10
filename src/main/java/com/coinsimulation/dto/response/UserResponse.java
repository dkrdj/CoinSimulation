package com.coinsimulation.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String nickname;
    private String profile;
    private Double cash;
}
