package com.coinsimulation.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@NoArgsConstructor
@Table("users")
public class User {
    @Id
    private Long id;
    private String nickname;
    private String role;
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


    public void updateNickName(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfile(String profile) {
        this.profile = profile;
    }

}
