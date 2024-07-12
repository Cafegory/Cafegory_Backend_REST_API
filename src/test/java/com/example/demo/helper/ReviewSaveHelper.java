package com.example.demo.helper;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.review.Review;
import com.example.demo.factory.TestReviewFactory;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.review.ReviewRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class ReviewSaveHelper {

	private final ReviewRepository reviewRepository;
	private final CafeRepository cafeRepository;
	private final MemberRepository memberRepository;

	public Review saveReview(Cafe cafe, Member member) {
		Cafe mergedCafe = cafeRepository.save(cafe);
		Member mergedMember = memberRepository.save(member);
		Review review = TestReviewFactory.createReview(mergedCafe, mergedMember);
		return reviewRepository.save(review);
	}
}
