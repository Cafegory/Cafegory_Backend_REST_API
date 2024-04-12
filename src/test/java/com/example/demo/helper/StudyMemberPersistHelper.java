package com.example.demo.helper;

import com.example.demo.builder.TestStudyMemberBuilder;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.helper.entitymanager.EntityManagerForPersistHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StudyMemberPersistHelper {

	private final EntityManagerForPersistHelper<StudyMember> em;

	public StudyMember persistDefaultStudyMember(Member member, StudyOnce study) {
		StudyMember studyMember = new TestStudyMemberBuilder().build(member, study);
		return em.save(studyMember);
	}

}
