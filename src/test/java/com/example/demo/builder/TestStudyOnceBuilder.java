package com.example.demo.builder;

import java.time.LocalDateTime;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.study.StudyOnce;

public class TestStudyOnceBuilder {

	private Long id;
	private String name = "카페 스터디";
	private Cafe cafe;
	private LocalDateTime startDateTime = LocalDateTime.of(2999, 2, 16, 12, 0);
	private LocalDateTime endDateTime = LocalDateTime.of(2999, 2, 16, 15, 0);
	private int maxMemberCount = 5;
	private int nowMemberCount = 0;
	private boolean isEnd = false;
	private boolean ableToTalk = true;
	private MemberImpl leader;

	public TestStudyOnceBuilder id(Long id) {
		this.id = id;
		return this;
	}

	public TestStudyOnceBuilder name(String name) {
		this.name = name;
		return this;
	}

	public TestStudyOnceBuilder cafe(Cafe cafe) {
		this.cafe = cafe;
		return this;
	}

	public TestStudyOnceBuilder startDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
		return this;
	}

	public TestStudyOnceBuilder endDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
		return this;
	}

	public TestStudyOnceBuilder maxMemberCount(int maxMemberCount) {
		this.maxMemberCount = maxMemberCount;
		return this;
	}

	public TestStudyOnceBuilder nowMemberCount(int nowMemberCount) {
		this.nowMemberCount = nowMemberCount;
		return this;
	}

	public TestStudyOnceBuilder end() {
		this.isEnd = true;
		return this;
	}

	public TestStudyOnceBuilder talkImpossible() {
		this.ableToTalk = false;
		return this;
	}

	public TestStudyOnceBuilder leader(MemberImpl leader) {
		this.leader = leader;
		return this;
	}

	public StudyOnce build() {
		StudyOnce studyOnce = StudyOnce.builder()
			.id(id)
			.name(name)
			.cafe(cafe)
			.startDateTime(startDateTime)
			.endDateTime(endDateTime)
			.maxMemberCount(maxMemberCount)
			.nowMemberCount(nowMemberCount)
			.isEnd(isEnd)
			.ableToTalk(ableToTalk)
			.leader(leader)
			.build();
		return studyOnce;
	}
}
