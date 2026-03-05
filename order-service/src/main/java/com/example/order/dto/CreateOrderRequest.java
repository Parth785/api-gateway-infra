package com.example.order.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreateOrderRequest {

    private Long userId;

    private List<OrderItemRequest> items;
}

