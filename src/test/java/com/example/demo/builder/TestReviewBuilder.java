package com.example.demo.builder;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.review.Review;

public class TestReviewBuilder {

	private Long id;
	private String content = "커피가 맛있고 공부하기 좋아요!!";
	private double rate = 4.9;
	private Cafe cafe;
	private Member member;

	public TestReviewBuilder id(Long id) {
		this.id = id;
		return this;
	}

	public TestReviewBuilder content(String content) {
		this.content = content;
		return this;
	}

	public TestReviewBuilder rate(double rate) {
		this.rate = rate;
		return this;
	}

	public TestReviewBuilder cafe(Cafe cafe) {
		this.cafe = cafe;
		return this;
	}

	public TestReviewBuilder member(Member member) {
		this.member = member;
		return this;
	}

	public Review build() {
		return Review.builder()
			.id(id)
			.content(content)
			.rate(rate)
			.cafe(cafe)
			.member(member)
			.build();
	}
}
