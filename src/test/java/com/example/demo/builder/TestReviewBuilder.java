package com.example.demo.builder;

import com.example.demo.domain.MemberImpl;
import com.example.demo.domain.ReviewImpl;
import com.example.demo.domain.cafe.CafeImpl;

public class TestReviewBuilder {

	private Long id;
	private String content = "커피가 맛있고 공부하기 좋아요!!";
	private double rate = 4.9;
	private CafeImpl cafe;
	private MemberImpl member;

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

	public TestReviewBuilder cafe(CafeImpl cafe) {
		this.cafe = cafe;
		return this;
	}

	public TestReviewBuilder member(MemberImpl member) {
		this.member = member;
		return this;
	}

	public ReviewImpl build() {
		return ReviewImpl.builder()
			.id(id)
			.content(content)
			.rate(rate)
			.cafe(cafe)
			.member(member)
			.build();
	}
}
