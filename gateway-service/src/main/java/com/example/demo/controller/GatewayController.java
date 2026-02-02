package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/")
public class GatewayController {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    // Route users requests
    @GetMapping("/users/{id}")
    public ResponseEntity<Map> getUserById(@PathVariable String id) {
        String userServiceUrl = "http://localhost:8083/users/" + id;
        Map response = restTemplate.getForObject(userServiceUrl, Map.class);
        return ResponseEntity.ok(response);
    }
    
    // Route orders requests
    @GetMapping("/orders/{id}")
    public ResponseEntity<Map> getOrderById(@PathVariable String id) {
        String orderServiceUrl = "http://localhost:8082/orders/" + id;
        Map response = restTemplate.getForObject(orderServiceUrl, Map.class);
        return ResponseEntity.ok(response);
    }
}
