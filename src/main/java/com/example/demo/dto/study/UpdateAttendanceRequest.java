package com.example.demo.dto.study;

import java.util.List;

import lombok.Getter;

@Getter
public class UpdateAttendanceRequest {

	private List<StudyMemberStateRequest> states;

	public UpdateAttendanceRequest() {
	}

	public UpdateAttendanceRequest(StudyMemberStateRequest... states) {
		this.states = List.of(states);
	}
}
