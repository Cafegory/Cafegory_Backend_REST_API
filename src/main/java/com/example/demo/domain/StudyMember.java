package com.example.demo.domain;

import java.time.LocalDateTime;

import javax.persistence.ConstraintMode;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "study_member")
public class StudyMember {
	@EmbeddedId
	private StudyMemberId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@MapsId("memberId")
	private MemberImpl member;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@MapsId("studyId")
	private StudyOnceImpl study;
	@Enumerated(EnumType.STRING)
	@Setter
	private Attendance attendance;

	@Builder
	public StudyMember(MemberImpl member, StudyOnceImpl study) {
		this.member = member;
		this.study = study;
		id = new StudyMemberId(member.getId(), study.getId());
		attendance = Attendance.YES;
	}

	public boolean isConflictWith(LocalDateTime start, LocalDateTime end) {
		LocalDateTime studyStartDateTime = study.getStartDateTime();
		LocalDateTime studyEndDateTime = study.getEndDateTime();
		return (start.isBefore(studyEndDateTime) || start.isEqual(studyEndDateTime)) && (studyStartDateTime.isBefore(
			end) || studyStartDateTime.isEqual(end));
	}
}
