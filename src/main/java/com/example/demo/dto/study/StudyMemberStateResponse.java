package com.example.demo.dto.study;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyMemberStateResponse {

	private final Long memberId;
	private final boolean attendance;
	private final LocalDateTime lastUpdateTime;

}
