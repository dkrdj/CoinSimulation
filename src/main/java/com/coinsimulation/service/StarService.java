package com.coinsimulation.service;

import com.coinsimulation.entity.Star;
import com.coinsimulation.repository.StarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StarService {
    private final StarRepository starRepository;

    public Flux<Star> getStars(Long userId) {
        return starRepository.findByUserId(userId);
    }

    public Mono<Star> setStar(Long userId, String code) {
        return starRepository.save(Star.builder().userId(userId).code(code).build());
    }

    public Mono<Void> setUnStar(Long userId, String code) {
        return starRepository.deleteByUserIdAndCode(userId, code);
    }
}
