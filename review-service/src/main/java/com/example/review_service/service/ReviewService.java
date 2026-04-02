package com.example.review_service.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.example.review_service.dto.ReviewRequest;
import com.example.review_service.dto.ReviewResponse;
import com.example.review_service.dto.ReviewSummary;
import com.example.review_service.entity.Review;
import com.example.review_service.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private ReviewResponse mapToResponse(Review review) {
        return new ReviewResponse(
            review.getId(),
            review.getProductId(),
            review.getUserId(),
            review.getUserName(),
            review.getRating(),
            review.getComment(),
            review.getCreatedAt()
        );
    }

    public ReviewResponse createReview(Long userId, String userName, ReviewRequest request) {
        // check if user already reviewed this product
        reviewRepository.findByProductIdAndUserId(request.getProductId(), userId)
            .ifPresent(r -> {
                throw new RuntimeException("You have already reviewed this product");
            });

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Review review = new Review();
        review.setProductId(request.getProductId());
        review.setUserId(userId);
        review.setUserName(userName);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return mapToResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ReviewResponse> getUserReviews(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ReviewSummary getProductSummary(Long productId) {
        Double avg = reviewRepository.getAverageRating(productId);
        Long total = reviewRepository.countByProductId(productId);

        return new ReviewSummary(
            productId,
            avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0,
            total,
            reviewRepository.countByProductIdAndRating(productId, 5),
            reviewRepository.countByProductIdAndRating(productId, 4),
            reviewRepository.countByProductIdAndRating(productId, 3),
            reviewRepository.countByProductIdAndRating(productId, 2),
            reviewRepository.countByProductIdAndRating(productId, 1)
        );
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this review");
        }

        reviewRepository.deleteById(reviewId);
    }
}