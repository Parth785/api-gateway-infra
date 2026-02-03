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

 // In RateLimiterService
    public boolean isAllowed(String ipAddress) {
        String key = "rate:limit:" + ipAddress;
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(WINDOW_SECONDS));
        }
        
        // Logging
        if (count != null) {
            if (count > LIMIT) {
                System.out.println("❌ BLOCKED: " + ipAddress + " (" + count + "/" + LIMIT + " requests)");
            } else {
                System.out.println("✅ ALLOWED: " + ipAddress + " (" + count + "/" + LIMIT + " requests)");
            }
        }
        
        return count != null && count <= LIMIT;
    }
}
