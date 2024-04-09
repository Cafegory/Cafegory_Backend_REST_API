package com.example.demo.mapper;

import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.dto.study.StudyOnceCommentInfo;
import com.example.demo.dto.study.StudyOnceReplyResponse;

public class StudyOnceCommentMapper {

	public StudyOnceCommentInfo toStudyOnceCommentInfo(StudyOnceComment comment) {
		return new StudyOnceCommentInfo(comment.getId(), comment.getContent());
	}

	public StudyOnceReplyResponse toStudyOnceReplyResponse(StudyOnceComment comment) {
		return new StudyOnceReplyResponse(comment.getId(), comment.getContent());
	}
}
