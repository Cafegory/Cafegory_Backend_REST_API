package com.example.demo.dto.study;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.dto.member.MemberResponse;

import lombok.Getter;

@Getter
public class StudyOnceCommentsSearchResponse {

	private MemberResponse replyWriter;
	private List<StudyOnceCommentSearchResponse> comments = new ArrayList<>();

	public StudyOnceCommentsSearchResponse(MemberResponse replyWriter) {
		this.replyWriter = replyWriter;
	}

	public void addStudyOnceCommentSearchResponse(StudyOnceCommentSearchResponse studyOnceCommentSearchResponse) {
		comments.add(studyOnceCommentSearchResponse);
	}
}
