package com.example.demo.service;

import com.example.demo.dto.StudyOnceCommentRequest;
import com.example.demo.dto.StudyOnceCommentUpdateRequest;

public interface StudyOnceCommentService {

	Long saveQuestion(Long memberId, Long studyOnceId, StudyOnceCommentRequest request);

	void updateComment(Long memberId, Long studyOnceCommentId, StudyOnceCommentUpdateRequest request);

	void deleteQuestion(Long studyOnceQuestionId);

	boolean isPersonWhoAskedComment(Long memberId, Long studyOnceCommentId);

	Long saveReply(Long memberId, Long studyOnceId, Long parentStudyOnceCommentId, StudyOnceCommentRequest request);
}
