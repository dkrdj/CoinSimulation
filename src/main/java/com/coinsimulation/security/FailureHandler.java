package com.coinsimulation.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class FailureHandler implements ServerAuthenticationFailureHandler {
    private final ObjectMapper om;
    private final DataBufferFactory factory;

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        return webFilterExchange.getExchange().getResponse().writeWith(
                Mono.just(exception.getMessage())
                        .handle((err, sink) -> {
                            try {
                                sink.next(om.writeValueAsBytes(err));
                            } catch (JsonProcessingException e) {
                                sink.error(new RuntimeException(e));
                            }
                        })
                        .cast(byte[].class)
                        .map(err -> factory.wrap(err)));
    }
}
