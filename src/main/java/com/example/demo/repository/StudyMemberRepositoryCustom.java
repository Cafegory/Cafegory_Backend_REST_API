package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.domain.StudyMember;
import com.example.demo.domain.member.MemberImpl;

public interface StudyMemberRepositoryCustom {
	List<StudyMember> findByMemberAndStudyDate(MemberImpl member, LocalDate studyDate);
}
