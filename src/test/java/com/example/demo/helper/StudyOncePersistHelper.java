package com.example.demo.helper;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.demo.builder.TestStudyOnceBuilder;
import com.example.demo.domain.cafe.CafeImpl;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.study.StudyOnceImpl;

public class StudyOncePersistHelper {

	@PersistenceContext
	private EntityManager em;

	public StudyOnceImpl persistDefaultStudyOnce(CafeImpl cafe, MemberImpl leader) {
		StudyOnceImpl studyOnce = new TestStudyOnceBuilder().cafe(cafe).leader(leader).build();
		em.persist(studyOnce);
		return studyOnce;
	}

	public StudyOnceImpl persistStudyOnceWithTime(CafeImpl cafe, MemberImpl leader, LocalDateTime startDateTime,
		LocalDateTime endDateTime) {
		StudyOnceImpl studyOnce = new TestStudyOnceBuilder().cafe(cafe)
			.leader(leader)
			.startDateTime(startDateTime)
			.endDateTime(endDateTime)
			.build();
		em.persist(studyOnce);
		return studyOnce;
	}

}
