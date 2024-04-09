package com.example.demo.helper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.demo.builder.TestStudyMemberBuilder;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyOnceImpl;

public class StudyMemberPersistHelper {

	@PersistenceContext
	private EntityManager em;

	public StudyMember persistDefaultStudyMember(MemberImpl member, StudyOnceImpl study) {
		StudyMember studyMember = new TestStudyMemberBuilder().build(member, study);
		em.persist(studyMember);
		return studyMember;
	}

}
