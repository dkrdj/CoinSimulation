package com.coinsimulation.controller;

import com.coinsimulation.dto.response.UserResponse;
import com.coinsimulation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("rank")
public class RankController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<Flux<UserResponse>> getRank() {
        return ResponseEntity.ok(userService.getTop10Users());
    }
}
