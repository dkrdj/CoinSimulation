package com.coinsimulation.controller;

import com.coinsimulation.entity.Execution;
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
    public Flux<ResponseEntity<Execution>> getExecution(@SessionAttribute("user") Long userId) {
        return executionService.selectExecution(userId).map(ResponseEntity::ok);
    }
}
