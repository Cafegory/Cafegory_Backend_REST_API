package com.example.demo.service;

import static com.example.demo.exception.ExceptionType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.MemberImpl;
import com.example.demo.domain.ReviewImpl;
import com.example.demo.domain.cafe.CafeImpl;
import com.example.demo.dto.ReviewSaveRequest;
import com.example.demo.dto.ReviewUpdateRequest;
import com.example.demo.exception.CafegoryException;
import com.example.demo.exception.ExceptionType;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.cafe.CafeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

	private final CafeRepository cafeRepository;
	private final MemberRepository memberRepository;
	private final ReviewRepository reviewRepository;

	@Override
	public Long saveReview(Long memberId, Long cafeId, ReviewSaveRequest request) {
		ReviewImpl review = ReviewImpl.builder()
			.content(request.getContent())
			.rate(request.getRate())
			.cafe(findCafeById(cafeId))
			.member(findMemberById(memberId))
			.build();
		ReviewImpl savedReview = reviewRepository.save(review);
		return savedReview.getId();
	}

	@Override
	public void updateReview(Long memberId, Long reviewId, ReviewUpdateRequest request) {
		ReviewImpl findReview = findReviewById(reviewId);
		MemberImpl findMember = findMemberById(memberId);
		validateReviewer(findReview, findMember);

		findReview.updateContent(request.getContent());
		findReview.updateRate(request.getRate());
	}

	private static void validateReviewer(ReviewImpl findReview, MemberImpl findMember) {
		if (!findReview.isValidMember(findMember)) {
			throw new CafegoryException(ExceptionType.REVIEW_INVALID_MEMBER);
		}
	}

	private ReviewImpl findReviewById(Long reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CafegoryException(REVIEW_NOT_FOUND));
	}

	@Override
	public void deleteReview(Long memberId, Long reviewId) {
		ReviewImpl findReview = findReviewById(reviewId);
		MemberImpl findMember = findMemberById(memberId);
		validateReviewer(findReview, findMember);

		reviewRepository.delete(findReview);
	}

	private MemberImpl findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new CafegoryException(MEMBER_NOT_FOUND));
	}

	private CafeImpl findCafeById(Long cafeId) {
		return cafeRepository.findById(cafeId)
			.orElseThrow(() -> new CafegoryException(CAFE_NOT_FOUND));
	}

}


