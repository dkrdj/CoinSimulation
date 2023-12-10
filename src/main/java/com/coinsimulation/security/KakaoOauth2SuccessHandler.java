package com.coinsimulation.security;

import com.coinsimulation.dto.response.LoginResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOauth2SuccessHandler implements ServerAuthenticationSuccessHandler {
    private final ObjectMapper om;
    private final DataBufferFactory factory;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        UserDetailsCustom userDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(securityContext -> securityContext.setAuthentication(authentication))
                .then(webFilterExchange.getExchange().getSession()
                        .doOnNext(session ->
                                session.getAttributes().put(
                                        "user",
                                        userDetailsCustom.getUserId())
                        ))
                .then(webFilterExchange.getExchange().getResponse()
                        .writeWith(Mono.just(LoginResponse.builder()
                                        .nickname(userDetailsCustom.getName())
                                        .profile(userDetailsCustom.getProfile())
                                        .build())
                                .handle((user, sink) -> {
                                    try {
                                        sink.next(om.writeValueAsBytes(user));
                                    } catch (JsonProcessingException e) {
                                        sink.error(new RuntimeException(e));
                                    }
                                })
                                .cast(byte[].class)
                                .map(user -> factory.wrap(user))));
    }
}
