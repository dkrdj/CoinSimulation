package com.coinsimulation.security;

import com.coinsimulation.dto.request.LoginRequest;
import com.coinsimulation.entity.User;
import com.coinsimulation.exception.LoginException;
import com.coinsimulation.repository.UserRepository;
import com.coinsimulation.util.S3Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServerAuthenticationCustomConverter implements ServerAuthenticationConverter {
    private final UserRepository userRepository;
    private final S3Utils s3Utils;
    private final ObjectMapper om;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getRequest().getBody()
                .handle((buffer, sink) ->
                        {
                            byte[] bytes = new byte[buffer.readableByteCount()];
                            buffer.read(bytes);
                            try {
                                LoginRequest loginRequest = om.readValue(bytes, LoginRequest.class);
                                if (!StringUtils.hasText(loginRequest.getNickname()) || !StringUtils.hasText(loginRequest.getProfile())) {
                                    sink.error(new LoginException());
                                }
                                log.info(loginRequest.toString());
                                sink.next(loginRequest);
                            } catch (IOException e) {
                                sink.error(new LoginException());
                            }
                        }
                )
                .single()
                .cast(LoginRequest.class)
                .flatMap(loginRequest -> userRepository.findByProviderId(loginRequest.getProviderId())
                        .switchIfEmpty(userRepository.save(User.builder()
                                .providerId(loginRequest.getProviderId())
                                .profile(loginRequest.getProfile())
                                .nickname(loginRequest.getNickname())
                                .role("USER")
                                .cash(30000000d)
                                .build()))
                        .flatMap(user ->
                                s3Utils.uploadObjectFromUrl(
                                                user.getProfile(),
                                                user.getId())
                                        .map(fileResponse -> {
                                            user.setProfile(fileResponse.path());
                                            return user;
                                        }))
                        .flatMap(userRepository::save)
                        .map(UserDetailsCustom::new)
                        .map(userDetailsCustom ->
                                UsernamePasswordAuthenticationToken
                                        .authenticated(
                                                userDetailsCustom,
                                                null,
                                                new ArrayList<>(Collections.singleton(new SimpleGrantedAuthority("USER"))))
                        )
                );
    }
}
