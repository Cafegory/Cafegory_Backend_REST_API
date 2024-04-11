package com.example.demo.builder;

import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.study.Attendance;
import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyOnce;

public class TestStudyMemberBuilder {

	private MemberImpl member;
	private StudyOnce study;
	private Attendance attendance = Attendance.YES;

	public TestStudyMemberBuilder member(MemberImpl member) {
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

	public StudyMember build(MemberImpl member, StudyOnce study) {
		return StudyMember.builder()
			.member(member)
			.study(study)
			.build();
	}
}
