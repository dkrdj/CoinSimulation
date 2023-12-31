package com.coinsimulation.util;

import com.coinsimulation.dto.common.UploadStatus;
import com.coinsimulation.dto.response.FileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
@Slf4j
public class S3Utils {
    private final S3AsyncClient s3AsyncClient;
    private final WebClient webClient;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Mono<FileResponse> uploadObjectFromUrl(String url, Long userId) {
        return webClient.get()
                .uri(url)
                .exchangeToMono(response -> response.bodyToMono(byte[].class)
                        .map(data -> new DefaultFilePart(data, response.headers().contentType().orElseThrow())))
                .flatMap(defaultFilePart -> uploadObject(defaultFilePart, userId));
    }

    public Mono<FileResponse> uploadObject(FilePart filePart, Long userId) {
        String filename = URLEncoder.encode(filePart.filename(), StandardCharsets.UTF_8);
        String key = userId + "/" + filename;

        Map<String, String> metadata = Map.of("filename", filename);
        // get media type
        MediaType mediaType = ObjectUtils.defaultIfNull(filePart.headers().getContentType(), MediaType.APPLICATION_OCTET_STREAM);
        CompletableFuture<CreateMultipartUploadResponse> s3AsyncClientMultipartUpload = s3AsyncClient
                .createMultipartUpload(CreateMultipartUploadRequest.builder()
                        .contentType(mediaType.toString())
                        .metadata(metadata)
                        .key(key)
                        .bucket(bucket)
                        .build());
        UploadStatus uploadStatus = new UploadStatus(Objects.requireNonNull(filePart.headers().getContentType()).toString(), key);
        Flux<DataBuffer> buffer = Mono.fromFuture(s3AsyncClientMultipartUpload)
                .flatMapMany(response -> {
                    FileUtils.checkSdkResponse(response);
                    uploadStatus.setUploadId(response.uploadId());
                    log.info("Upload object with ID={}", response.uploadId());
                    return filePart.content();
                });
        return uploadFromDataBuffer(buffer, uploadStatus, filename);
    }

    private Mono<FileResponse> uploadFromDataBuffer(Flux<DataBuffer> buffer, UploadStatus uploadStatus, String filename) {
        return buffer.bufferUntil(dataBuffer -> {
                    // Collect incoming values into multiple List buffers that will be emitted by the resulting Flux each time the given predicate returns true.
                    uploadStatus.addBuffered(dataBuffer.readableByteCount());
                    if (uploadStatus.getBuffered() > 0) {
                        log.info("BufferUntil - returning true, bufferedBytes={}, partCounter={}, uploadId={}",
                                uploadStatus.getBuffered(), uploadStatus.getPartCounter(), uploadStatus.getUploadId());

                        // reset buffer
                        uploadStatus.setBuffered(0);
                        return true;
                    }

                    return false;
                })
                .map(FileUtils::dataBufferToByteBuffer)
                // upload part
                .flatMap(byteBuffer -> uploadPartObject(uploadStatus, byteBuffer))
                .onBackpressureBuffer()
                .reduce(uploadStatus, (status, completedPart) -> {
                    log.info("Completed: PartNumber={}, etag={}", completedPart.partNumber(), completedPart.eTag());
                    (status).getCompletedParts().put(completedPart.partNumber(), completedPart);
                    return status;
                })
                .flatMap(uploadStatus1 -> completeMultipartUpload(uploadStatus))
                .map(response -> {
                    FileUtils.checkSdkResponse(response);
                    log.info("upload result: {}", response.toString());
                    return new FileResponse(filename, uploadStatus.getUploadId(), response.location(), uploadStatus.getContentType(), response.eTag());
                });
    }


    private Mono<CompletedPart> uploadPartObject(UploadStatus uploadStatus, ByteBuffer buffer) {
        final int partNumber = uploadStatus.getAddedPartCounter();
        log.info("UploadPart - partNumber={}, contentLength={}", partNumber, buffer.capacity());

        CompletableFuture<UploadPartResponse> uploadPartResponseCompletableFuture = s3AsyncClient.uploadPart(UploadPartRequest.builder()
                        .bucket(bucket)
                        .key(uploadStatus.getFileKey())
                        .partNumber(partNumber)
                        .uploadId(uploadStatus.getUploadId())
                        .contentLength((long) buffer.capacity())
                        .build(),
                AsyncRequestBody.fromPublisher(Mono.just(buffer)));

        return Mono
                .fromFuture(uploadPartResponseCompletableFuture)
                .map(uploadPartResult -> {
                    FileUtils.checkSdkResponse(uploadPartResult);
                    log.info("UploadPart - complete: part={}, etag={}", partNumber, uploadPartResult.eTag());
                    return CompletedPart.builder()
                            .eTag(uploadPartResult.eTag())
                            .partNumber(partNumber)
                            .build();
                });
    }

    private Mono<CompleteMultipartUploadResponse> completeMultipartUpload(UploadStatus uploadStatus) {
        log.info("CompleteUpload - fileKey={}, completedParts.size={}",
                uploadStatus.getFileKey(), uploadStatus.getCompletedParts().size());

        CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder()
                .parts(uploadStatus.getCompletedParts().values())
                .build();

        return Mono.fromFuture(s3AsyncClient.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                .bucket(bucket)
                .uploadId(uploadStatus.getUploadId())
                .multipartUpload(multipartUpload)
                .key(uploadStatus.getFileKey())
                .build()));
    }
}
