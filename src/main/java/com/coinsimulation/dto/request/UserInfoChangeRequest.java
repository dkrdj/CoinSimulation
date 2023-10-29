package com.coinsimulation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoChangeRequest {
    private String nickname;
    private String role;
    private String profile;
    private Boolean isNew;
}
