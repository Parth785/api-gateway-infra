package com.example.demo.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int LIMIT = 5; // Allow only 5 requests
    private static final int WINDOW_SECONDS = 60; // Per 60 seconds

    public boolean isAllowed(String ipAddress) {
        String key = "rate:limit:" + ipAddress;
        
        // 1. Increment the value by 1
        Long count = redisTemplate.opsForValue().increment(key);

        // 2. If it's the first request in this window, set expiration
        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(WINDOW_SECONDS));
        }

        // 3. If count is more than limit, block
        return count != null && count <= LIMIT;
    }
}
