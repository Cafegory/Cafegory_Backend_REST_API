package com.example.demo.domain.member;

import static com.example.demo.exception.ExceptionType.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.example.demo.domain.study.StudyMember;
import com.example.demo.exception.CafegoryException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member")
public class MemberImpl implements Member {

	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;
	private String name;

	private String email;
	private String introduction;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "thumbnail_image_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ThumbnailImage thumbnailImage;
	@Transient
	@Builder.Default
	// 회원은 오래 활동한 경우 매우 많은 스터디에 참여했을 수 있다. 따라서 이를 무작정 모두 조회하면 메모리 오버플로우가 발생할 수 있다.
	// 별도의 레퍼지토리를 통해 적절한 범위의 StudyMember 엔티티를 조회한 뒤 주입해야 한다.
	@Setter
	private List<StudyMember> studyMembers = new ArrayList<>();

	@Override
	public void addStudyMember(StudyMember studyMember) {
		studyMembers.add(studyMember);
	}

	public void updateProfile(String name, String introduction) {
		validateIntroduction(introduction);
		this.name = name;
		this.introduction = introduction;
	}

	private void validateIntroduction(String introduction) {
		if (introduction.length() > 300) {
			throw new CafegoryException(PROFILE_UPDATE_INVALID_INTRODUCTION);
		}
	}

}
