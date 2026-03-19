package com.example.product.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CreateProductRequest {

    private String name;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private String description;
    private String imageUrl;
    private String modelUrl;
}