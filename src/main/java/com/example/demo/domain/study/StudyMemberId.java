package com.example.demo.domain.study;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class StudyMemberId implements Serializable {
	@Column(name = "member_id")
	private Long memberId;

	@Column(name = "study_id")
	private Long studyId;
}
