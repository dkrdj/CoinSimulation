package com.coinsimulation.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@Table("users")
public class User implements Serializable {
    @Id
    private Long id;
    @Setter
    private String nickname;
    private String role;
    @Setter
    private String profile;
    private Long providerId;

    @Builder
    public User(Long id, String nickname, String role, String profile, Long providerId) {
        this.id = id;
        this.nickname = nickname;
        this.role = role;
        this.profile = profile;
        this.providerId = providerId;
    }

}
