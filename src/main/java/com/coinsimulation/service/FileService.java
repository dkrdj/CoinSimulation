package com.coinsimulation.service;

import com.coinsimulation.dto.response.FileResponse;
import com.coinsimulation.util.TempUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class FileService {
    private final TempUtil tempUtil;

    public Mono<FileResponse> test(FilePart filePart, String path) {
        return tempUtil.uploadObject(filePart, path);
    }
}
