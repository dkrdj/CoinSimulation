package com.coinsimulation.controller;

import com.coinsimulation.dto.UserDto;
import com.coinsimulation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Mono<UserDto>> getUserInfo(@SessionAttribute("user") Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }
}
