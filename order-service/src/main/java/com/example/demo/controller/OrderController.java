package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
	
	@GetMapping("/{id}")
    public Map<String, Object> getOrder(@PathVariable String id) {
        Map<String, Object> order = new HashMap<>();
        order.put("id", id);
        order.put("name", "Dummy order");
        order.put("service", "order-service");
        return order;
    }

}
