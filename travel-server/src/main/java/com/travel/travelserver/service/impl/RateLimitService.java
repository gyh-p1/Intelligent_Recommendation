package com.travel.travelserver.service.impl;

import com.travel.travelserver.common.ClientIdUtils;
import com.travel.travelserver.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {
    private final StringRedisTemplate stringRedisTemplate;
    private final Map<String, LocalCounter> fallbackCounters = new ConcurrentHashMap<>();

    public void check(String bucket, String clientId, int limitPerMinute) {
        String safeClientId = ClientIdUtils.resolve(clientId);
        String key = "travel:rate:%s:%s".formatted(bucket, safeClientId);
        try {
            Long count = stringRedisTemplate.opsForValue().increment(key);
            if (count != null && count == 1L) {
                stringRedisTemplate.expire(key, Duration.ofMinutes(1));
            }
            if (count != null && count > limitPerMinute) {
                throw BusinessException.tooManyRequests();
            }
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.debug("Redis rate limit skipped for key {}, using local fallback", key, exception);
            checkLocal(key, limitPerMinute);
        }
    }

    private void checkLocal(String key, int limitPerMinute) {
        Instant now = Instant.now();
        LocalCounter counter = fallbackCounters.compute(key, (ignored, current) -> {
            if (current == null || current.expireAt().isBefore(now)) {
                return new LocalCounter(new AtomicInteger(0), now.plus(Duration.ofMinutes(1)));
            }
            return current;
        });
        if (counter.count().incrementAndGet() > limitPerMinute) {
            throw BusinessException.tooManyRequests();
        }
    }

    private record LocalCounter(AtomicInteger count, Instant expireAt) {
    }
}
