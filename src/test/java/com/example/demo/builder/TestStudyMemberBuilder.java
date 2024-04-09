package com.example.demo.builder;

import com.example.demo.domain.Attendance;
import com.example.demo.domain.StudyMember;
import com.example.demo.domain.StudyOnceImpl;
import com.example.demo.domain.member.MemberImpl;

public class TestStudyMemberBuilder {

	private MemberImpl member;
	private StudyOnceImpl study;
	private Attendance attendance = Attendance.YES;

	public TestStudyMemberBuilder member(MemberImpl member) {
		this.member = member;
		return this;
	}

	public TestStudyMemberBuilder study(StudyOnceImpl study) {
		this.study = study;
		return this;
	}

	public TestStudyMemberBuilder attendanceYes() {
		this.attendance = Attendance.YES;
		return this;
	}

	public StudyMember build(MemberImpl member, StudyOnceImpl study) {
		return StudyMember.builder()
			.member(member)
			.study(study)
			.build();
	}
}
