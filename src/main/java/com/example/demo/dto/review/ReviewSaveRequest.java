package com.example.demo.dto.review;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

import lombok.Getter;

@Getter
public class ReviewSaveRequest {

	@NotBlank
	private String content;
	@PositiveOrZero
	private double rate;

	public ReviewSaveRequest() {
	}

	public ReviewSaveRequest(String content, double rate) {
		this.content = content;
		this.rate = rate;
	}
}
