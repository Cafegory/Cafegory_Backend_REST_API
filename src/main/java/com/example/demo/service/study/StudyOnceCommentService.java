package com.example.demo.service.study;

import com.example.demo.dto.study.StudyOnceCommentRequest;
import com.example.demo.dto.study.StudyOnceCommentUpdateRequest;

public interface StudyOnceCommentService {

	Long saveQuestion(Long memberId, Long studyOnceId, StudyOnceCommentRequest request);

	void updateQuestion(Long memberId, Long studyOnceCommentId, StudyOnceCommentUpdateRequest request);

	void deleteQuestion(Long studyOnceCommentId);

	boolean isPersonWhoAskedComment(Long memberId, Long studyOnceCommentId);

	Long saveReply(Long memberId, Long studyOnceId, Long parentStudyOnceCommentId, StudyOnceCommentRequest request);

	void deleteReply(Long studyOnceCommentId);

	void updateReply(Long memberId, Long studyOnceCommentId, StudyOnceCommentUpdateRequest request);

}
