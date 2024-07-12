package com.example.demo.domain.member;

import static com.example.demo.exception.ExceptionType.*;

import java.time.LocalDateTime;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.example.demo.domain.study.StudyMember;
import com.example.demo.exception.CafegoryException;

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
@Table(name = "member")
public class Member {

	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;
	private String name;

	private String email;
	private String introduction;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "thumbnail_image_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ThumbnailImage thumbnailImage;

	@Builder.Default
	@OneToMany(mappedBy = "member")
	private List<StudyMember> studyMembers = new ArrayList<>();

	public void addStudyMember(StudyMember studyMember) {
		this.studyMembers.add(studyMember);
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

	public boolean hasStudyScheduleConflict(LocalDateTime start, LocalDateTime end) {
		return this.studyMembers.stream()
			.anyMatch(studyMember -> studyMember.isConflictWith(start, end));
	}

}
