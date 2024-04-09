package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.dto.member.MemberResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyOnceCommentSearchResponse {

	private MemberResponse questionWriter;
	private StudyOnceCommentInfo questionInfo;
	private List<StudyOnceReplyResponse> replies = new ArrayList<>();

	public void addStudyOnceReplyResponse(StudyOnceReplyResponse studyOnceReplyResponse) {
		replies.add(studyOnceReplyResponse);
	}

}
