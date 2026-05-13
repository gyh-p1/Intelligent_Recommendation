package com.travel.travelserver.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public <T> Optional<T> get(String key, TypeReference<T> typeReference) {
        try {
            String value = stringRedisTemplate.opsForValue().get(key);
            if (value == null || value.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(value, typeReference));
        } catch (Exception exception) {
            log.debug("Redis get skipped for key {}", key, exception);
            return Optional.empty();
        }
    }

    public void set(String key, Object value, Duration ttl) {
        try {
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), ttl);
        } catch (Exception exception) {
            log.debug("Redis set skipped for key {}", key, exception);
        }
    }

    public void delete(String key) {
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception exception) {
            log.debug("Redis delete skipped for key {}", key, exception);
        }
    }
}
