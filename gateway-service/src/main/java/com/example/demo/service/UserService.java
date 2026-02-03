package com.example.demo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Cacheable(value = "users", key = "#id")
    public Map fetchUserFromRemote(String id) {
        System.out.println("Fetching user from remote Service for ID: " + id);
        String userServiceUrl = "http://localhost:8083/users/" + id;
        return restTemplate.getForObject(userServiceUrl, Map.class);
    }

    @Cacheable(value = "orders", key = "#id")
    public Map fetchOrderFromRemote(String id) {
        System.out.println("Fetching order from remote Service for ID: " + id);
        String orderServiceUrl = "http://localhost:8082/orders/" + id;
        return restTemplate.getForObject(orderServiceUrl, Map.class);
    }
}
