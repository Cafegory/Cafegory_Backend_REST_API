package com.example.demo.dto.study;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateAttendanceResponse {

	private final List<StudyMemberStateResponse> states;

}
