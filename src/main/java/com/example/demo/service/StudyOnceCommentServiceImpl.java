package com.example.demo.service;

import static com.example.demo.exception.ExceptionType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.MemberImpl;
import com.example.demo.domain.StudyOnceComment;
import com.example.demo.domain.StudyOnceImpl;
import com.example.demo.dto.StudyOnceCommentRequest;
import com.example.demo.dto.StudyOnceCommentUpdateRequest;
import com.example.demo.exception.CafegoryException;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.StudyOnceCommentRepository;
import com.example.demo.repository.StudyOnceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyOnceCommentServiceImpl implements StudyOnceCommentService {

	private final StudyOnceCommentRepository studyOnceCommentRepository;
	private final MemberRepository memberRepository;
	private final StudyOnceRepository studyOnceRepository;

	@Override
	public Long saveQuestion(Long memberId, Long studyOnceId, StudyOnceCommentRequest request) {
		StudyOnceComment question = StudyOnceComment.builder()
			.content(request.getContent())
			.member(findMemberById(memberId))
			.studyOnce(findStudyOnceById(studyOnceId))
			.build();
		StudyOnceComment savedQuestion = studyOnceCommentRepository.save(question);
		return savedQuestion.getId();
	}

	@Override
	public void updateQuestion(Long memberId, Long studyOnceCommentId, StudyOnceCommentUpdateRequest request) {
		StudyOnceComment question = findStudyOnceCommentById(studyOnceCommentId);
		validatePersonAsked(memberId, studyOnceCommentId);
		question.changeContent(request.getContent());
	}

	@Override
	public boolean isPersonWhoAskedComment(Long memberId, Long studyOnceCommentId) {
		StudyOnceComment question = findStudyOnceCommentById(studyOnceCommentId);
		return question.isPersonAsked(findMemberById(memberId));
	}

	@Override
	public Long saveReply(Long memberId, Long studyOnceId, Long parentStudyOnceCommentId,
		StudyOnceCommentRequest request) {
		StudyOnceImpl studyOnce = findStudyOnceById(studyOnceId);
		MemberImpl member = findMemberById(memberId);
		if (!studyOnce.isLeader(member)) {
			throw new CafegoryException(STUDY_ONCE_REPLY_PERMISSION_DENIED);
		}
		StudyOnceComment parentComment = findStudyOnceCommentById(parentStudyOnceCommentId);
		if (parentComment.hasParentComment()) {
			throw new CafegoryException(STUDY_ONCE_SINGLE_REPLY_PER_QUESTION);
		}
		if (!parentComment.hasParentComment() && parentComment.hasReply()) {
			throw new CafegoryException(STUDY_ONCE_PARENT_COMMENT_HAS_SINGLE_CHILD_COMMENT);
		}
		StudyOnceComment reply = StudyOnceComment.builder()
			.content(request.getContent())
			.member(member)
			.studyOnce(studyOnce)
			.parent(parentComment)
			.build();
		StudyOnceComment savedReply = studyOnceCommentRepository.save(reply);
		return savedReply.getId();
	}

	@Override
	public void deleteReply(Long memberId, Long studyOnceCommentId) {
		StudyOnceComment comment = findStudyOnceCommentById(studyOnceCommentId);
		validatePersonAsked(memberId, studyOnceCommentId);
		studyOnceCommentRepository.delete(comment);
	}

	@Override
	public void updateReply(Long memberId, Long studyOnceCommentId, StudyOnceCommentUpdateRequest request) {
		StudyOnceComment question = findStudyOnceCommentById(studyOnceCommentId);
		validatePersonAsked(memberId, studyOnceCommentId);
		question.changeContent(request.getContent());
	}

	private void validatePersonAsked(Long memberId, Long studyOnceCommentId) {
		if (!isPersonWhoAskedComment(memberId, studyOnceCommentId)) {
			throw new CafegoryException(STUDY_ONCE_COMMENT_PERMISSION_DENIED);
		}
	}

	@Override
	public void deleteQuestion(Long studyOnceQuestionId) {
		StudyOnceComment question = findStudyOnceCommentById(studyOnceQuestionId);
		studyOnceCommentRepository.delete(question);
	}

	private StudyOnceComment findStudyOnceCommentById(Long studyOnceCommentId) {
		return studyOnceCommentRepository.findById(studyOnceCommentId)
			.orElseThrow(() -> new CafegoryException(STUDY_ONCE_COMMENT_NOT_FOUND));
	}

	private MemberImpl findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new CafegoryException(MEMBER_NOT_FOUND));
	}

	private StudyOnceImpl findStudyOnceById(long studyOnceId) {
		return studyOnceRepository.findById(studyOnceId)
			.orElseThrow(() -> new CafegoryException(STUDY_ONCE_NOT_FOUND));
	}
}
