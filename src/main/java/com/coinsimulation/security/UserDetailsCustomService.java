//package com.coinsimulation.security;
//
//import com.coinsimulation.exception.LoginException;
//import com.coinsimulation.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class UserDetailsCustomService implements ReactiveUserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public Mono<UserDetails> findByUsername(String username) {
//        return userRepository.findByProviderId(Long.parseLong(username))
//                .onErrorMap(throwable -> new LoginException())
//                .map(user -> new UserDetailsCustom(user));
//    }
//}
