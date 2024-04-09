package com.example.demo.builder;

import com.example.demo.domain.StudyOnceComment;
import com.example.demo.domain.StudyOnceImpl;
import com.example.demo.domain.member.MemberImpl;

public class TestStudyOnceCommentBuilder {
	private Long id;
	private String content = "몇시까지 공부하시나요?";
	private MemberImpl member;
	private StudyOnceImpl studyOnce;
	private StudyOnceComment parent;

	public TestStudyOnceCommentBuilder id(Long id) {
		this.id = id;
		return this;
	}

	public TestStudyOnceCommentBuilder content(String content) {
		this.content = content;
		return this;
	}

	public TestStudyOnceCommentBuilder member(MemberImpl member) {
		this.member = member;
		return this;
	}

	public TestStudyOnceCommentBuilder studyOnce(StudyOnceImpl studyOnce) {
		this.studyOnce = studyOnce;
		return this;
	}

	public TestStudyOnceCommentBuilder parent(StudyOnceComment parent) {
		this.parent = parent;
		return this;
	}

	public StudyOnceComment build() {
		return StudyOnceComment.builder()
			.id(id)
			.content(content)
			.member(member)
			.studyOnce(studyOnce)
			.parent(parent)
			.build();
	}

}
