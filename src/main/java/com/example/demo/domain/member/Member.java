package com.example.demo.domain.member;

import java.util.List;

import com.example.demo.domain.StudyMember;

public interface Member {
	Long getId();

	List<StudyMember> getStudyMembers();

	void addStudyMember(StudyMember studyMember);
}
