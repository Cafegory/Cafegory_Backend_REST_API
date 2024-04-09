package com.example.demo.dto.review;

import com.example.demo.dto.WriterResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewSearchResponse {

	private final long reviewId;
	private final WriterResponse writer;
	private final double rate;
	private final String content;
}
