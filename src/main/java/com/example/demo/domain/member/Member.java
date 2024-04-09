package com.example.demo.domain.member;

import java.util.List;

import com.example.demo.domain.study.StudyMember;

public interface Member {
	Long getId();

	List<StudyMember> getStudyMembers();

	void addStudyMember(StudyMember studyMember);
}
