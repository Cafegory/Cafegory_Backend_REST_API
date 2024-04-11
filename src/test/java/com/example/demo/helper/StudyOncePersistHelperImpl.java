package com.example.demo.helper;

import java.time.LocalDateTime;

import com.example.demo.builder.TestStudyOnceBuilder;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.repository.study.StudyOnceRepository;

public class StudyOncePersistHelperImpl extends StudyOncePersistHelper {
	private final StudyOnceRepository studyOnceRepository;

	public StudyOncePersistHelperImpl(StudyOnceRepository studyOnceRepository) {
		this.studyOnceRepository = studyOnceRepository;
	}

	@Override
	public StudyOnce persistDefaultStudyOnce(Cafe cafe, Member leader) {
		StudyOnce studyOnce = new TestStudyOnceBuilder().cafe(cafe).leader(leader).build();
		return studyOnceRepository.save(studyOnce);
	}

	@Override
	public StudyOnce persistStudyOnceWithTime(Cafe cafe, Member leader, LocalDateTime startDateTime,
		LocalDateTime endDateTime) {
		StudyOnce studyOnce = new TestStudyOnceBuilder().cafe(cafe)
			.leader(leader)
			.startDateTime(startDateTime)
			.endDateTime(endDateTime)
			.build();
		return studyOnceRepository.save(studyOnce);
	}
}
