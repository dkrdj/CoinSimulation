package com.coinsimulation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.web.server.ServerAuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class WebFluxSecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            ServerAuthorizationRequestRepository<OAuth2AuthorizationRequest> repository,
                                                            CorsConfigurationSource corsConfig) {
        return http
                .cors(cors -> cors.configurationSource(corsConfig))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/test/**").permitAll()
                        .anyExchange().authenticated()
                )
                //흠
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .oauth2Login(oAuth2LoginSpec ->
                        oAuth2LoginSpec.authenticationSuccessHandler(
                                        (webFilterExchange, authentication) -> {
                                            log.info("oauth2 login success");
                                            webFilterExchange.getExchange()
                                                    .getResponse()
                                                    .addCookie(ResponseCookie.from("sessionId", "success")
                                                            .build());
                                            ReactiveSecurityContextHolder.getContext()
                                                    .subscribe(contextHolder -> contextHolder.setAuthentication(authentication));
                                            return Mono.empty();
                                        }
                                )
                                .authorizationRequestRepository(repository)
                )
                .build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5500"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
