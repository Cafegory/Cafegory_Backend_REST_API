package com.example.demo.helper;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.demo.builder.TestStudyOnceBuilder;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.study.StudyOnce;

public class StudyOncePersistHelper {

	@PersistenceContext
	private EntityManager em;

	public StudyOnce persistDefaultStudyOnce(Cafe cafe, MemberImpl leader) {
		StudyOnce studyOnce = new TestStudyOnceBuilder().cafe(cafe).leader(leader).build();
		em.persist(studyOnce);
		return studyOnce;
	}

	public StudyOnce persistStudyOnceWithTime(Cafe cafe, MemberImpl leader, LocalDateTime startDateTime,
		LocalDateTime endDateTime) {
		StudyOnce studyOnce = new TestStudyOnceBuilder().cafe(cafe)
			.leader(leader)
			.startDateTime(startDateTime)
			.endDateTime(endDateTime)
			.build();
		em.persist(studyOnce);
		return studyOnce;
	}

}
