package com.example.review_service.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long productId;
    private Integer rating;
    private String comment;
}
