package com.example.product.dto;

import lombok.Data;

//Add this DTO to your order-service dto folder
//Used when calling Product Service to reduce stock
@Data
public class StockUpdateRequest {

 private Long productId;
 private Integer quantity; // send negative value to reduce stock e.g. -2
}