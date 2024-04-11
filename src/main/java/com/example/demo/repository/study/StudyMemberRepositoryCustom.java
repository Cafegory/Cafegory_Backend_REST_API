package com.example.demo.repository.study;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.StudyMember;

public interface StudyMemberRepositoryCustom {
	List<StudyMember> findByMemberAndStudyDate(Member member, LocalDate studyDate);
}
