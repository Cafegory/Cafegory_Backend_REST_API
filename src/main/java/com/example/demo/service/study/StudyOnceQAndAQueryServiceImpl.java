package com.example.demo.service.study;

import static com.example.demo.exception.ExceptionType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.dto.WriterResponse;
import com.example.demo.dto.study.StudyOnceCommentResponse;
import com.example.demo.exception.CafegoryException;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.mapper.StudyOnceMapper;
import com.example.demo.repository.study.StudyOnceCommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyOnceQAndAQueryServiceImpl implements StudyOnceQAndAQueryService {

	private final StudyOnceCommentRepository studyOnceCommentRepository;
	private final MemberMapper memberMapper;
	private final StudyOnceMapper studyOnceMapper;

	@Override
	public StudyOnceCommentResponse searchComment(Long studyOnceCommentId) {
		StudyOnceComment question = findStudyOnceCommentById(studyOnceCommentId);
		WriterResponse writerResponse = memberMapper.toWriterResponse(question.getMember());
		return studyOnceMapper.toStudyOnceQuestionResponse(question, writerResponse);
	}

	private StudyOnceComment findStudyOnceCommentById(Long studyOnceCommentId) {
		return studyOnceCommentRepository.findById(studyOnceCommentId)
			.orElseThrow(() -> new CafegoryException(STUDY_ONCE_COMMENT_NOT_FOUND));
	}
}
