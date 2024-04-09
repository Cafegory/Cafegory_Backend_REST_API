package com.example.demo.dto.study;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CanMakeStudyOnceResponse {
	// 기능 구현 완료되기전까지 임시 dto

	private final String startTime = "10:00";
	private final String endTime = "11:00";
	private final int maxMemberCount = 1;
}
