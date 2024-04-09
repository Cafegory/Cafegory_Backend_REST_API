package com.example.demo.repository.study;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.study.StudyMember;

public interface StudyMemberRepositoryCustom {
	List<StudyMember> findByMemberAndStudyDate(MemberImpl member, LocalDate studyDate);
}
