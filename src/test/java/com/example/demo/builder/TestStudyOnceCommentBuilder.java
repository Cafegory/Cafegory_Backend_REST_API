package com.example.demo.builder;

import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.domain.study.StudyOnceComment;

public class TestStudyOnceCommentBuilder {
	private Long id;
	private String content = "몇시까지 공부하시나요?";
	private MemberImpl member;
	private StudyOnce studyOnce;
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

	public TestStudyOnceCommentBuilder studyOnce(StudyOnce studyOnce) {
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
