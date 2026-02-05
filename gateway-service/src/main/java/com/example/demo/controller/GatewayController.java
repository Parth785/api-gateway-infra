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

import com.example.demo.JWT.JwtUtil;
import com.example.demo.service.RateLimiterService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class GatewayController {
    
	@Autowired
    private RateLimiterService rateLimiterService;
    
    @Autowired
    private JwtUtil jwtUtil;  // Add this to validate JWT tokens
    
    @Autowired
    private UserService userService;

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable String id, HttpServletRequest request) {
        
        // 1. CHECK JWT TOKEN FIRST
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Missing or invalid Authorization header"));
        }
        
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid or expired token"));
        }
        
        // Extract username (optional, for logging)
        String username = jwtUtil.extractUsername(token);
        System.out.println("✅ Authenticated user: " + username);
        
        // 2. THEN CHECK RATE LIMIT
        String clientIp = request.getRemoteAddr();
        if (clientIp.equals("0:0:0:0:0:0:0:1") || clientIp.equals("127.0.0.1")) {
            clientIp = "localhost";
        }
        
        if (!rateLimiterService.isAllowed(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("error", "Rate limit exceeded"));
        }

        // 3. FORWARD TO BACKEND
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
    	        .body(Map.of(
    	            "error", "Rate limit exceeded",
    	            "message", "Too many requests from " + clientIp + ". Try again in 60 seconds.",
    	            "limit", 5,
    	            "window", "60 seconds",
    	            "retryAfter", 60
    	        ));
    	}
        Map orderBody = userService.fetchOrderFromRemote(id); 
        return ResponseEntity.ok(orderBody);
    }
}
