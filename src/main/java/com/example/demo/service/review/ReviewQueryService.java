package com.example.demo.service.review;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.review.ReviewResponse;
import com.example.demo.dto.review.ReviewSearchRequest;
import com.example.demo.dto.review.ReviewSearchResponse;

public interface ReviewQueryService {

	PagedResponse<ReviewSearchResponse> searchWithPagingByCafeId(ReviewSearchRequest request);

	ReviewResponse searchOne(Long reviewId);
}
