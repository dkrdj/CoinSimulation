package com.coinsimulation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginRequest {
    private Long providerId;
    private String nickname;
    private String profile;
}
