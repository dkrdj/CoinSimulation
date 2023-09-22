//package com.coinsimulation.util;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Schedulers;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3AsyncClient;
//import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectResponse;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.URL;
//import java.time.LocalDate;
//import java.util.Objects;
//
//@Component
//@RequiredArgsConstructor
//public class FileUtil {
//    private final S3AsyncClient amazonS3Client;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    // Other methods...
//
//    public Mono<String> upload(MultipartFile multipartFile, String dirName) {
//        return convert(multipartFile)
//                .map(uploadFile -> upload(uploadFile, dirName))
//                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));
//    }
//
//    private Mono<String> upload(File uploadFile, String dirName) {
//        String fileName = dirName + "/" + Math.random() * 1000 + "_" + LocalDate.now() + uploadFile.getName();
//        return putS3(uploadFile, fileName)
//                .doOnSuccess(response -> uploadFile.delete()) // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)
//                .map(response -> amazonS3Client.utilities().getUrl(r -> r.bucket(bucket).key(fileName)).toString()); // 업로드 된 파일의 S3 URL 주소 반환
//    }
//
//    public Mono<String> urlUpload(String url, String dirName) throws IOException {
//
//        URL profile = new URL(url);
//
//        String fileName = dirName + "/" + Math.random() * 1000 + "_" + "socialprofile"; // 이미지 이름
//        String ext = url.substring(url.lastIndexOf('.') + 1); // 확장자
//
//        BufferedImage img = ImageIO.read(profile);
//        //파일 저장 경로 생성
//        File uploadFile = new File("upload/" + fileName + "." + ext);
//        if (!uploadFile.exists()) {
//            uploadFile.mkdirs();
//        }
//
//        ImageIO.write(img, ext, uploadFile);
//
//        return putS3(uploadFile, fileName)
//                .doOnSuccess(response -> uploadFile.delete())
//                .map(response ->
//                        amazonS3Client.utilities().getUrl(r -> r.bucket(bucket).key(fileName)).toString());
//    }
//
//    private Mono<PutObjectResponse> putS3(File file, String key) {
//        PutObjectRequest request =
//                PutObjectRequest.builder()
//                        .bucket(bucket)
//                        .key(key)
//                        .acl(ObjectCannedACL.PUBLIC_READ)
//                        .build();
//
//        return Mono.fromFuture(amazonS3Client.putObject(request, RequestBody.fromBytes(file.getBytes())));
//    }
//
//    private Mono<File> convert(MultipartFile file) {
//        return Mono.fromCallable(() -> {
//            File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
//            if (convertFile.createNewFile()) {
//                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
//                    fos.write(file.getBytes());
//                }
//                return convertFile;
//            } else {
//                throw new IOException("Failed to create a new file.");
//            }
//        }).subscribeOn(Schedulers.boundedElastic());
//    }
//
//    // convert method...
//}
