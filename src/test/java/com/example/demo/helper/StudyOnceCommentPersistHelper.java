package com.example.demo.helper;

import com.example.demo.builder.TestStudyOnceCommentBuilder;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.helper.entitymanager.EntityManagerForPersistHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StudyOnceCommentPersistHelper {

	private final EntityManagerForPersistHelper<StudyOnceComment> em;

	public StudyOnceComment persistDefaultStudyOnceQuestion(Member member, StudyOnce studyOnce) {
		StudyOnceComment studyOnceComment = new TestStudyOnceCommentBuilder().member(member)
			.studyOnce(studyOnce)
			.build();
		return em.save(studyOnceComment);
	}

	public StudyOnceComment persistStudyOnceQuestionWithContent(Member member, StudyOnce studyOnce,
		String content) {
		StudyOnceComment studyOnceComment = new TestStudyOnceCommentBuilder().member(member)
			.studyOnce(studyOnce)
			.content(content)
			.build();
		return em.save(studyOnceComment);
	}

	public StudyOnceComment persistStudyOnceReplyWithContent(Member member, StudyOnce studyOnce,
		StudyOnceComment parent, String content) {
		StudyOnceComment reply = new TestStudyOnceCommentBuilder().member(member)
			.studyOnce(studyOnce)
			.parent(parent)
			.content(content)
			.build();
		return em.save(reply);
	}

}
