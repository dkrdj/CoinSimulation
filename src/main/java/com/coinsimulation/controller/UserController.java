package com.coinsimulation.controller;

import com.coinsimulation.dto.request.UserInfoChangeRequest;
import com.coinsimulation.dto.response.UserResponse;
import com.coinsimulation.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public Mono<ResponseEntity<UserResponse>> getUserInfo(@SessionAttribute("user") Long userId) {
        return userService.getUserInfo(userId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping
    public Mono<ResponseEntity<UserResponse>> changeUserInfo(@SessionAttribute("user") Long userId, @RequestBody UserInfoChangeRequest request) {
        return userService.changeUserInfo(userId, request)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("profile")
    public Mono<ResponseEntity<UserResponse>> changeUserProfile(@SessionAttribute("user") Long userId, @RequestPart("profile") Mono<FilePart> filePart) {
        return filePart.flatMap(part -> userService.changeUserProfile(part, userId))
                .map(ResponseEntity::ok);
    }


}
