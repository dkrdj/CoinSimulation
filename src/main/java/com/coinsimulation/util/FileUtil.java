//package com.coinsimulation.util;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//import software.amazon.awssdk.core.async.AsyncRequestBody;
//import software.amazon.awssdk.services.s3.S3AsyncClient;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectResponse;
//
//import java.util.concurrent.CompletableFuture;
//
//@Component
//@RequiredArgsConstructor
//public class FileUtil {
//    private final S3AsyncClient s3AsyncClient;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    public CompletableFuture<PutObjectResponse> uploadImageToS3(String imageUrl, String bucketName, String key) {
//        WebClient webClient = WebClient.builder().build();
//        Mono<byte[]> mono = webClient.get()
//                .uri(imageUrl)
//                .accept(MediaType.APPLICATION_OCTET_STREAM)
//                .exchangeToMono(response -> response.bodyToMono(byte[].class));
//        return webClient.get()
//                .uri(imageUrl)
//                .accept(MediaType.APPLICATION_OCTET_STREAM)
//                .exchangeToMono(response -> response.bodyToMono(byte[].class))
//                .then(data -> {
//                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                            .bucket(bucketName)
//                            .key(key)
//                            .build();
//
//                    return s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromBytes(data));
//                });
//    }
//}
