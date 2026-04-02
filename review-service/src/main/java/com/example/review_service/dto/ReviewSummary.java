package com.example.review_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSummary {
    private Long productId;
    private Double averageRating;
    private Long totalReviews;
    private Long fiveStar;
    private Long fourStar;
    private Long threeStar;
    private Long twoStar;
    private Long oneStar;
}