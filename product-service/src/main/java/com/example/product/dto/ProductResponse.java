package com.example.product.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
}