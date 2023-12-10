package com.coinsimulation.entity;

import lombok.*;
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
}
