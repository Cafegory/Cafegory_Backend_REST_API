package com.example.demo.service.review;

import static com.example.demo.exception.ExceptionType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.review.Review;
import com.example.demo.dto.review.ReviewSaveRequest;
import com.example.demo.dto.review.ReviewUpdateRequest;
import com.example.demo.exception.CafegoryException;
import com.example.demo.exception.ExceptionType;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.review.ReviewRepository;

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
		Review review = Review.builder()
			.content(request.getContent())
			.rate(request.getRate())
			.cafe(findCafeById(cafeId))
			.member(findMemberById(memberId))
			.build();
		Review savedReview = reviewRepository.save(review);
		return savedReview.getId();
	}

	@Override
	public void updateReview(Long memberId, Long reviewId, ReviewUpdateRequest request) {
		Review findReview = findReviewById(reviewId);
		Member findMember = findMemberById(memberId);
		validateReviewer(findReview, findMember);

		findReview.updateContent(request.getContent());
		findReview.updateRate(request.getRate());
	}

	private static void validateReviewer(Review findReview, Member findMember) {
		if (!findReview.isValidMember(findMember)) {
			throw new CafegoryException(ExceptionType.REVIEW_INVALID_MEMBER);
		}
	}

	private Review findReviewById(Long reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CafegoryException(REVIEW_NOT_FOUND));
	}

	@Override
	public void deleteReview(Long memberId, Long reviewId) {
		Review findReview = findReviewById(reviewId);
		Member findMember = findMemberById(memberId);
		validateReviewer(findReview, findMember);

		reviewRepository.delete(findReview);
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new CafegoryException(MEMBER_NOT_FOUND));
	}

	private Cafe findCafeById(Long cafeId) {
		return cafeRepository.findById(cafeId)
			.orElseThrow(() -> new CafegoryException(CAFE_NOT_FOUND));
	}

}


