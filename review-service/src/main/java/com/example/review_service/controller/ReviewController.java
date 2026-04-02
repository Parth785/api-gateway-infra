package com.example.review_service.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.review_service.dto.ReviewRequest;
import com.example.review_service.dto.ReviewResponse;
import com.example.review_service.dto.ReviewSummary;
import com.example.review_service.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // POST /reviews — submit review
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName,
            @RequestBody ReviewRequest request) {
        return ResponseEntity.status(201)
                .body(reviewService.createReview(userId, userName, request));
    }

    // GET /reviews/product/{id} — all reviews for a product
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(
            @PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    // GET /reviews/product/{id}/summary — avg rating + breakdown
    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<ReviewSummary> getProductSummary(
            @PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductSummary(productId));
    }

    // GET /reviews/user — my reviews
    @GetMapping("/user")
    public ResponseEntity<List<ReviewResponse>> getUserReviews(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(reviewService.getUserReviews(userId));
    }

    // DELETE /reviews/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }
}