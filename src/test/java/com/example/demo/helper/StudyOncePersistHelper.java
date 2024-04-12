package com.example.demo.helper;

import java.time.LocalDateTime;

import com.example.demo.builder.TestStudyOnceBuilder;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.helper.entitymanager.EntityManagerForPersistHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StudyOncePersistHelper {

	private final EntityManagerForPersistHelper<StudyOnce> em;

	public StudyOnce persistDefaultStudyOnce(Cafe cafe, Member leader) {
		StudyOnce studyOnce = new TestStudyOnceBuilder().cafe(cafe).leader(leader).build();
		return em.save(studyOnce);
	}

	public StudyOnce persistStudyOnceWithTime(Cafe cafe, Member leader, LocalDateTime startDateTime,
		LocalDateTime endDateTime) {
		StudyOnce studyOnce = new TestStudyOnceBuilder().cafe(cafe)
			.leader(leader)
			.startDateTime(startDateTime)
			.endDateTime(endDateTime)
			.build();
		return em.save(studyOnce);
	}

}
