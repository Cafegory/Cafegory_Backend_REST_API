package com.example.demo.domain.study;

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

import com.example.demo.domain.BaseEntity;
import com.example.demo.domain.member.MemberImpl;

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
@Table(name = "study_once_comment")
public class StudyOnceComment extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "study_once_comment_id")
	private Long id;

	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_study_once_comment_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private StudyOnceComment parent;

	@OneToMany(mappedBy = "parent")
	@Builder.Default
	private List<StudyOnceComment> children = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private MemberImpl member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_once_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private StudyOnceImpl studyOnce;

	@Builder
	private StudyOnceComment(Long id, String content, MemberImpl member, StudyOnceImpl studyOnce) {
		//todo content에 대한 검증 추가
		this.id = id;
		this.content = content;
		this.member = member;
		this.studyOnce = studyOnce;
	}

	public void addReply(StudyOnceComment reply) {
		this.children.add(reply);
		reply.parent = this;
	}

	public void changeContent(String content) {
		//todo content 에 대한 검증 추가
		this.content = content;
	}

	public boolean isPersonAsked(MemberImpl member) {
		return this.member.getId().equals(member.getId());
	}

	public boolean hasReply() {
		return !this.children.isEmpty();
	}

	public boolean hasParentComment() {
		return this.parent != null;
	}

}
