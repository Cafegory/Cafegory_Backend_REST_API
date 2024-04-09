package com.example.demo.dto.review;

import com.example.demo.dto.PagedRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewSearchRequest extends PagedRequest {

	private final Long cafeId;
}
