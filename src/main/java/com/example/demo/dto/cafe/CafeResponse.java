package com.example.demo.dto.cafe;

import java.util.List;

import com.example.demo.dto.review.ReviewResponse;
import com.example.demo.dto.study.CanMakeStudyOnceResponse;
import com.example.demo.dto.study.StudyOnceForCafeResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CafeResponse {

	private final CafeBasicInfoResponse basicInfo;
	private final List<ReviewResponse> review;
	private final List<StudyOnceForCafeResponse> meetings;
	private final List<CanMakeStudyOnceResponse> canMakeMeeting;
}
