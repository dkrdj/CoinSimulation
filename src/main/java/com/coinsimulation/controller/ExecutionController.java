package com.coinsimulation.controller;

import com.coinsimulation.dto.response.ExecutionResponse;
import com.coinsimulation.service.ExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("execution")
@RequiredArgsConstructor
public class ExecutionController {
    private final ExecutionService executionService;

    @GetMapping
    public ResponseEntity<Flux<ExecutionResponse>> getExecution(@SessionAttribute("user") Long userId) {
        return ResponseEntity.ok(executionService.selectExecution(userId));
    }
}
