package com.coinsimulation.security;


import com.coinsimulation.entity.User;
import com.coinsimulation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoUserService extends DefaultReactiveOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Mono<OAuth2User> oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(oAuth2User);
    }

    private Mono<OAuth2User> processOAuth2User(Mono<OAuth2User> oAuth2User) {
        Mono<Map<String, Object>> attributes = oAuth2User.map(OAuth2AuthenticatedPrincipal::getAttributes);

        Mono<User> userMono = attributes.map(KakaoUserInfo::new)
                .flatMap(kakaoUser -> userRepository.findByProviderId(kakaoUser.getProviderId()))
                .switchIfEmpty(attributes.map(KakaoUserInfo::new)
                        .flatMap(kakaoUser -> userRepository.save(User.builder()
                                .providerId(kakaoUser.getProviderId())
                                .nickname(kakaoUser.getNickname())
                                .profile(kakaoUser.getProfile())
                                .role("USER")
                                .build())));

        return Mono.zip(userMono, attributes)
                .map(o -> new UserDetailsCustom(o.getT1(), o.getT2()));
    }
}