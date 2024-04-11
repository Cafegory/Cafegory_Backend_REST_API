package com.example.demo.builder;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.Attendance;
import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyOnce;

public class TestStudyMemberBuilder {

	private Member member;
	private StudyOnce study;
	private Attendance attendance = Attendance.YES;

	public TestStudyMemberBuilder member(Member member) {
		this.member = member;
		return this;
	}

	public TestStudyMemberBuilder study(StudyOnce study) {
		this.study = study;
		return this;
	}

	public TestStudyMemberBuilder attendanceYes() {
		this.attendance = Attendance.YES;
		return this;
	}

	public StudyMember build(Member member, StudyOnce study) {
		return StudyMember.builder()
			.member(member)
			.study(study)
			.build();
	}
}
