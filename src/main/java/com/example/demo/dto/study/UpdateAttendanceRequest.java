package com.example.demo.dto.study;

import java.util.List;

import lombok.Getter;

@Getter
public class UpdateAttendanceRequest {

	private List<StudyMemberStateRequest> states;

	public UpdateAttendanceRequest() {
	}

	public UpdateAttendanceRequest(List<StudyMemberStateRequest> states) {
		this.states = states;
	}
}
