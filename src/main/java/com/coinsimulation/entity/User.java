package com.coinsimulation.entity;

import com.coinsimulation.dto.response.UserResponse;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
@Builder
public class User implements Serializable {
    @Id
    private Long id;
    @Setter
    private String nickname;
    @Setter
    private String role;
    @Setter
    private String profile;
    private Long providerId;
    @Setter
    private Double cash;

    public UserResponse toResponse() {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(this, userResponse);
        return userResponse;
    }
}
