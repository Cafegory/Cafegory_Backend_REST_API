package com.example.demo.helper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.demo.builder.TestStudyOnceCommentBuilder;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.domain.study.StudyOnceImpl;

public class StudyOnceCommentPersistHelper {

	@PersistenceContext
	private EntityManager em;

	public StudyOnceComment persistDefaultStudyOnceQuestion(MemberImpl member, StudyOnceImpl studyOnce) {
		StudyOnceComment studyOnceComment = new TestStudyOnceCommentBuilder().member(member)
			.studyOnce(studyOnce)
			.build();
		em.persist(studyOnceComment);
		return studyOnceComment;
	}

	public StudyOnceComment persistStudyOnceQuestionWithContent(MemberImpl member, StudyOnceImpl studyOnce,
		String content) {
		StudyOnceComment studyOnceComment = new TestStudyOnceCommentBuilder().member(member)
			.studyOnce(studyOnce)
			.content(content)
			.build();
		em.persist(studyOnceComment);
		return studyOnceComment;
	}

	public StudyOnceComment persistDefaultStudyOnceReply(MemberImpl member, StudyOnceImpl studyOnce,
		StudyOnceComment parent) {
		StudyOnceComment reply = new TestStudyOnceCommentBuilder().member(member)
			.studyOnce(studyOnce)
			.parent(parent)
			.build();
		em.persist(reply);
		return reply;
	}

	public StudyOnceComment persistStudyOnceReplyWithContent(MemberImpl member, StudyOnceImpl studyOnce,
		StudyOnceComment parent, String content) {
		StudyOnceComment reply = new TestStudyOnceCommentBuilder().member(member)
			.studyOnce(studyOnce)
			.parent(parent)
			.content(content)
			.build();
		em.persist(reply);
		return reply;
	}

}
