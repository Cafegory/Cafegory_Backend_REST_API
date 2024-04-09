package com.example.demo.dto.review;

import com.example.demo.dto.WriterResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponse {

	private final long reviewId;
	private final WriterResponse writer;
	private final double rate;
	private final String content;
}
