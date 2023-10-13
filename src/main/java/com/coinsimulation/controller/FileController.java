package com.coinsimulation.controller;

import com.coinsimulation.dto.response.FileResponse;
import com.coinsimulation.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("file")
@Slf4j
public class FileController {
    private final FileService fileService;

    @PostMapping("{path}")
    public ResponseEntity<Mono<FileResponse>> test(@RequestPart("file") Mono<FilePart> filePart, @PathVariable String path) {

        return ResponseEntity.ok(filePart.flatMap(part -> {
            log.info(filePart.toString());
            return fileService.test(part, path);
        }));
    }
}
