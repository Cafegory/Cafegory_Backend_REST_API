package com.example.demo.helper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.demo.builder.TestStudyMemberBuilder;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyOnce;

public class StudyMemberPersistHelper {

	@PersistenceContext
	private EntityManager em;

	public StudyMember persistDefaultStudyMember(MemberImpl member, StudyOnce study) {
		StudyMember studyMember = new TestStudyMemberBuilder().build(member, study);
		em.persist(studyMember);
		return studyMember;
	}

}
