package com.example.demo.helper;

import com.example.demo.builder.TestStudyOnceCommentBuilder;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.repository.study.StudyOnceCommentRepository;

public class StudyOnceCommentPersistHelperImpl extends StudyOnceCommentPersistHelper {
	private final StudyOnceCommentRepository studyOnceCommentRepository;

	public StudyOnceCommentPersistHelperImpl(StudyOnceCommentRepository studyOnceCommentRepository) {
		this.studyOnceCommentRepository = studyOnceCommentRepository;
	}

	@Override
	public StudyOnceComment persistDefaultStudyOnceQuestion(Member member, StudyOnce studyOnce) {
		StudyOnceComment studyOnceComment = new TestStudyOnceCommentBuilder().member(member)
			.studyOnce(studyOnce)
			.build();
		return studyOnceCommentRepository.save(studyOnceComment);
	}

	@Override
	public StudyOnceComment persistStudyOnceQuestionWithContent(Member member, StudyOnce studyOnce, String content) {
		StudyOnceComment studyOnceComment = new TestStudyOnceCommentBuilder().member(member)
			.studyOnce(studyOnce)
			.content(content)
			.build();
		return studyOnceCommentRepository.save(studyOnceComment);
	}

	@Override
	public StudyOnceComment persistStudyOnceReplyWithContent(Member member, StudyOnce studyOnce,
		StudyOnceComment parent, String content) {
		StudyOnceComment reply = new TestStudyOnceCommentBuilder().member(member)
			.studyOnce(studyOnce)
			.parent(parent)
			.content(content)
			.build();
		return studyOnceCommentRepository.save(reply);
	}
}
