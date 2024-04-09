package com.example.demo.dto.study;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class StudyMemberStateRequest {

	private Long memberId;
	private boolean attendance;
	private LocalDateTime lastUpdateTime;

	public StudyMemberStateRequest() {
	}

	public StudyMemberStateRequest(Long memberId, boolean attendance, LocalDateTime lastUpdateTime) {
		this.memberId = memberId;
		this.attendance = attendance;
		this.lastUpdateTime = lastUpdateTime;
	}
}
