package com.coinsimulation.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.time.Duration;

@Configuration
public class S3Config {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Bean
    public S3AsyncClient s3AsyncClient(AwsCredentialsProvider provider) {
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ZERO)
                .maxConcurrency(64)
                .build();
        S3Configuration serviceConfig = S3Configuration.builder()
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();
        S3AsyncClientBuilder builder = S3AsyncClient.builder()
                .httpClient(httpClient)
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(provider)
                .serviceConfiguration(serviceConfig);

        return builder.build();
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return () -> AwsBasicCredentials.create(accessKey, secretKey);
    }

}