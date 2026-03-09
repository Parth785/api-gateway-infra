package com.example.order.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class CreateOrderRequest {

//    private Long userId;

    private List<OrderItemRequest> items;
}

