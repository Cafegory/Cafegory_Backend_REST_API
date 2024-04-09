package com.example.demo.service.review;

import com.example.demo.dto.review.ReviewSaveRequest;
import com.example.demo.dto.review.ReviewUpdateRequest;

public interface ReviewService {

	Long saveReview(Long memberId, Long cafeId, ReviewSaveRequest request);

	void updateReview(Long memberId, Long reviewId, ReviewUpdateRequest request);

	void deleteReview(Long memberId, Long reviewId);
}
