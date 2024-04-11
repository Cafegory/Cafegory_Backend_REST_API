package com.example.demo.service.review;

import static com.example.demo.exception.ExceptionType.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.review.Review;
import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.review.ReviewResponse;
import com.example.demo.dto.review.ReviewSearchRequest;
import com.example.demo.dto.review.ReviewSearchResponse;
import com.example.demo.exception.CafegoryException;
import com.example.demo.mapper.ReviewMapper;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.review.ReviewRepository;
import com.example.demo.util.PageRequestCustom;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryServiceImpl implements ReviewQueryService {

	private final ReviewRepository reviewRepository;
	private final CafeRepository cafeRepository;
	private final ReviewMapper reviewMapper;

	@Override
	public PagedResponse<ReviewSearchResponse> searchWithPagingByCafeId(ReviewSearchRequest request) {
		validateExistCafe(request.getCafeId());
		Pageable pageable = PageRequestCustom.of(request.getPage(), request.getSizePerPage());
		Page<Review> pagedReviews = reviewRepository.findAllWithPagingByCafeId(request.getCafeId(),
			pageable);
		return createPagedResponse(pagedReviews,
			reviewMapper.toReviewSearchResponses(pagedReviews.getContent()));
	}

	@Override
	public ReviewResponse searchOne(Long reviewId) {
		return reviewMapper.toReviewResponse(findReviewById(reviewId));
	}

	private Review findReviewById(Long reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CafegoryException(REVIEW_NOT_FOUND));
	}

	private void validateExistCafe(Long cafeId) {
		if (!cafeRepository.existsById(cafeId)) {
			throw new CafegoryException(CAFE_NOT_FOUND);
		}
	}

	private PagedResponse<ReviewSearchResponse> createPagedResponse(Page<Review> pagedReviews,
		List<ReviewSearchResponse> reviewSearchResponse) {
		return PagedResponse.createWithFirstPageAsOne(
			pagedReviews.getNumber(),
			pagedReviews.getTotalPages(),
			pagedReviews.getNumberOfElements(),
			reviewSearchResponse
		);
	}

}
