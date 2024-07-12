package com.example.demo.helper;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.factory.TestStudyMemberFactory;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.study.StudyMemberRepository;
import com.example.demo.repository.study.StudyOnceRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class StudyMemberSaveHelper {

	private final StudyMemberRepository studyMemberRepository;
	private final MemberRepository memberRepository;
	private final StudyOnceRepository studyOnceRepository;

	public StudyMember saveStudyMember(Member member, StudyOnce studyOnce) {
		Member mergedMember = memberRepository.save(member);
		StudyOnce mergedStudyOnce = studyOnceRepository.save(studyOnce);
		StudyMember studyMember = TestStudyMemberFactory.createStudyMember(mergedMember, mergedStudyOnce);
		return studyMemberRepository.save(studyMember);
	}
}
