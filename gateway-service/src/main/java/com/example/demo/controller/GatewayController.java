package com.example.demo.controller;

import java.util.Map;

import org.apache.catalina.util.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.service.RateLimiterService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class GatewayController {
    
    @Autowired
    private RateLimiterService rateLimiterService;
    
    @Autowired
    private UserService userService; // This service should have @Cacheable

    @GetMapping("/users/{id}")
    // REMOVED @Cacheable from here!
    public ResponseEntity<Object> getUserById(@PathVariable String id, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
     // Normalize localhost for testing
        if (clientIp.equals("0:0:0:0:0:0:0:1") || clientIp.equals("127.0.0.1")) {
            clientIp = "localhost";
        }
        // 1. Rate Limiter ALWAYS runs now
        if (!rateLimiterService.isAllowed(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                 .body("Slow down! You've reached the limit.");
        }

        // 2. Fetch data (This method inside UserService has @Cacheable)
        Map userBody = userService.fetchUserFromRemote(id);
        return ResponseEntity.ok(userBody);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable String id, HttpServletRequest request) {
        // For learning, it's better to also move this logic to a Service 
        // and put @Cacheable there returning a Map.
    	String clientIp = request.getRemoteAddr();
    	// Normalize localhost for testing
    	if (clientIp.equals("0:0:0:0:0:0:0:1") || clientIp.equals("127.0.0.1")) {
    	    clientIp = "localhost";
    	}
    	if (!rateLimiterService.isAllowed(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                 .body("Slow down! You've reached the limit.");
        }
        Map orderBody = userService.fetchOrderFromRemote(id); 
        return ResponseEntity.ok(orderBody);
    }
}
