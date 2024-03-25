package com.example.demo.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "study_once_question")
public class StudyOnceQuestion extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "study_once_question_id")
	private Long id;

	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_study_once_question_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private StudyOnceQuestion parent;

	@OneToMany(mappedBy = "parent")
	@Builder.Default
	private List<StudyOnceQuestion> children = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private MemberImpl member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_once_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private StudyOnceImpl studyOnce;

	@Builder
	private StudyOnceQuestion(Long id, String content, MemberImpl member, StudyOnceImpl studyOnce) {
		//todo content에 대한 검증 추가
		this.id = id;
		this.content = content;
		this.member = member;
		this.studyOnce = studyOnce;
	}

	public void changeContent(String content) {
		//todo content 에 대한 검증 추가
		this.content = content;
	}

	public boolean isPersonAsked(MemberImpl member) {
		return this.member.getId().equals(member.getId());
	}

}
