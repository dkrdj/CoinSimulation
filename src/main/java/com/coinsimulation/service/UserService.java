package com.coinsimulation.service;

import com.coinsimulation.dto.request.UserInfoChangeRequest;
import com.coinsimulation.dto.response.UserResponse;
import com.coinsimulation.entity.User;
import com.coinsimulation.repository.UserRepository;
import com.coinsimulation.util.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final S3Utils s3Utils;

    public Mono<UserResponse> getUserInfo(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.toResponse());
    }

    @Transactional
    public Mono<UserResponse> changeUserInfo(Long userId, UserInfoChangeRequest request) {
        Mono<User> userMono = userRepository.findById(userId);
        return userMono.map(user -> {
                    if (StringUtils.hasText(request.getNickname())) {
                        user.setNickname(request.getNickname());
                    }
                    return user;
                })
                .flatMap(userRepository::save)
                .map(user -> user.toResponse());
    }

    @Transactional
    public Mono<UserResponse> changeUserProfile(FilePart filePart, Long userId) {
        return s3Utils.uploadObject(filePart, userId)
                .map(fileResponse -> fileResponse.path())
                .flatMap(path -> userRepository.findById(userId).map(user -> {
                    user.setProfile(path);
                    return user;
                }))
                .flatMap(user -> userRepository.save(user))
                .map(user -> user.toResponse());
    }

    public Flux<UserResponse> getTop10Users() {
        return userRepository.findTop10ByOrderByCashDesc().map(user -> user.toResponse());
    }
}
