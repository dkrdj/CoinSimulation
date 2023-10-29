package com.coinsimulation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.web.server.session.HeaderWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

@Configuration
@EnableRedisWebSession
public class HttpSessionConfig {

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(factory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        return redisTemplate;
//    }

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {

        RedisSerializationContext<String, Object> serializationContext =
                RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
                        .hashKey(new StringRedisSerializer())
                        .hashValue(new GenericJackson2JsonRedisSerializer())
                        .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        HeaderWebSessionIdResolver sessionIdResolver = new HeaderWebSessionIdResolver();
        sessionIdResolver.setHeaderName("sessionId");
        return sessionIdResolver;
    }
}
