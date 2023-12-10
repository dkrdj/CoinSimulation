package com.coinsimulation.controller;

import com.coinsimulation.dto.response.AssetResponse;
import com.coinsimulation.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("asset")
public class AssetController {
    private final AssetService assetService;

    @GetMapping
    public ResponseEntity<Flux<AssetResponse>> getAsset(@SessionAttribute("user") Long userId) {
        return ResponseEntity.ok(assetService.getAsset(userId));
    }

    @PostMapping("reset")
    public Mono<ResponseEntity<String>> resetAsset(@SessionAttribute("user") Long userId) {
        return assetService.resetCash(userId).map(ResponseEntity::ok);
    }
}
