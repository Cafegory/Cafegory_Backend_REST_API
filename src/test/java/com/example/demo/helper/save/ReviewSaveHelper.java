package com.example.demo.helper.save;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.demo.builder.TestReviewBuilder;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.review.Review;

public class ReviewSaveHelper {

	@PersistenceContext
	private EntityManager em;

	public Review persistDefaultReview(Cafe cafe, Member member) {
		Review review = new TestReviewBuilder().cafe(cafe).member(member).build();
		em.persist(review);
		return review;
	}
}
