package com.example.demo.dto.cafe;

import java.util.List;

import com.example.demo.dto.CanMakeStudyOnceResponse;
import com.example.demo.dto.StudyOnceForCafeResponse;
import com.example.demo.dto.review.ReviewResponse;

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
