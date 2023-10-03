package com.coinsimulation.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerAuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class KakaoAuthorizationRequestRepository implements ServerAuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;
    private final String KAKAO = "kakao";
    @Value("${kakao.token-uri}")
    private String tokenUri;
    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Override
    public Mono<OAuth2AuthorizationRequest> loadAuthorizationRequest(ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> removeAuthorizationRequest(ServerWebExchange exchange) {
        return this.clientRegistrationRepository.findByRegistrationId(KAKAO)
                .map(this::authorizationRequest);
    }


    private OAuth2AuthorizationRequest authorizationRequest(ClientRegistration clientRegistration) {
        return getBuilder(clientRegistration).clientId(clientRegistration.getClientId())
                .authorizationUri(tokenUri)
                .redirectUri(redirectUri)
                .scopes(clientRegistration.getScopes())
                .state(KAKAO)
                .build();
    }

    private OAuth2AuthorizationRequest.Builder getBuilder(ClientRegistration clientRegistration) {
        return OAuth2AuthorizationRequest.authorizationCode()
                .attributes((attrs) ->
                        attrs.put(OAuth2ParameterNames.REGISTRATION_ID, clientRegistration.getRegistrationId()));
    }
}
