package com.example.demo.helper;

import com.example.demo.builder.TestReviewBuilder;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.review.Review;
import com.example.demo.helper.entitymanager.EntityManagerForPersistHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewPersistHelper {

	private final EntityManagerForPersistHelper<Review> em;

	public Review persistDefaultReview(Cafe cafe, Member member) {
		Review review = new TestReviewBuilder().cafe(cafe).member(member).build();
		return em.save(review);
	}

}
