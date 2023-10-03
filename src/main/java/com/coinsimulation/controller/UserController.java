package com.coinsimulation.controller;

import com.coinsimulation.dto.request.UserInfoChangeRequest;
import com.coinsimulation.dto.response.UserResponse;
import com.coinsimulation.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Mono<UserResponse>> getUserInfo(@SessionAttribute("user") Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    @PatchMapping
    public ResponseEntity<Mono<UserResponse>> changeUserInfo(@SessionAttribute("user") Long userId, @RequestBody UserInfoChangeRequest request) {
        return ResponseEntity.ok(userService.changeUserInfo(userId, request)
                .then(userService.getUserInfo(userId)));
    }
}
