package com.coinsimulation.security;

import com.coinsimulation.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class UserDetailsCustom implements OAuth2User, Serializable {
    private final User user;
    private Map<String, Object> attributes;

    public UserDetailsCustom(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public Long getUserId() {
        return user.getId();
    }

    public Boolean getIsNew() {
        return user.getIsNew();
    }

    public String getProfile() {
        return user.getProfile();
    }

    @Override
    public String getName() {
        return user.getNickname();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(Collections.singleton(
                new SimpleGrantedAuthority(user.getRole())));
    }

}
