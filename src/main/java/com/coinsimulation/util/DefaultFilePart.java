package com.coinsimulation.util;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultFilePart implements FilePart {

    private final String name;
    private final byte[] content;
    private final MediaType contentType;

    public DefaultFilePart(byte[] content, MediaType contentType) {
        this.name = "file";
        this.content = content;
        this.contentType = contentType;
    }

    @Override
    public String filename() {
        return "kakao_profile";
    }

    @Override
    public Mono<Void> transferTo(Path dest) {
        return Mono.fromRunnable(() -> {
            try {
                Files.write(dest, content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.setContentLength(content.length);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "form-data; name=\"" + this.name + "\"; filename=\"" + filename() + "\"");
        return headers;
    }

    @Override
    public Flux<DataBuffer> content() {
        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(content);
        return Flux.just(dataBuffer);
    }
}