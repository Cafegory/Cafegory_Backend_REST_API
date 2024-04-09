package com.example.demo.helper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.demo.builder.TestReviewBuilder;
import com.example.demo.domain.cafe.CafeImpl;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.review.ReviewImpl;

public class ReviewPersistHelper {

	@PersistenceContext
	private EntityManager em;

	public ReviewImpl persistDefaultReview(CafeImpl cafe, MemberImpl member) {
		ReviewImpl review = new TestReviewBuilder().cafe(cafe).member(member).build();
		em.persist(review);
		return review;
	}

}
