package com.coinsimulation.controller;

import com.coinsimulation.entity.Star;
import com.coinsimulation.service.StarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("star")
public class StarController {

    private final StarService starService;

    @GetMapping
    public ResponseEntity<Flux<Star>> getStarredCoin(@SessionAttribute("user") Long userId) {
        return ResponseEntity.ok(starService.getStars(userId));
    }

    @PostMapping
    public Mono<ResponseEntity<Star>> makeStarredCoin(@SessionAttribute("user") Long userId, String code) {
        return starService.setStar(userId, code).map(ResponseEntity::ok);
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> deleteStarredCoin(@SessionAttribute("user") Long userId, String code) {
        return starService.setUnStar(userId, code).map(ResponseEntity::ok);
    }
}
