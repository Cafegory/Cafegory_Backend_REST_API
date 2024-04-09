package com.example.demo.dto.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberResponse {

	private final long memberId;
	private final String name;
	private final String thumbnailImg;
}
