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
    public ResponseEntity<Object> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.fetchUserFromRemote(id));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable String id) {
        return ResponseEntity.ok(userService.fetchOrderFromRemote(id));
    }
}
