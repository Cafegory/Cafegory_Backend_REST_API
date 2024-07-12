package com.example.demo.helper;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.factory.TestStudyOnceFactory;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.study.StudyOnceRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class StudyOnceSaveHelper {

	private final StudyOnceRepository studyOnceRepository;
	private final MemberRepository memberRepository;
	private final CafeRepository cafeRepository;

	public StudyOnce saveStudyOnce(Cafe cafe, Member leader) {
		Member mergedMember = memberRepository.save(leader);
		Cafe mergedCafe = cafeRepository.save(cafe);
		StudyOnce studyOnce = TestStudyOnceFactory.createStudyOnce(mergedCafe, mergedMember);
		return studyOnceRepository.save(studyOnce);
	}

	public StudyOnce saveStudyOnceWithTime(Cafe cafe, Member leader, LocalDateTime startDateTime,
		LocalDateTime endDateTime) {
		Member mergedLeader = memberRepository.save(leader);
		Cafe mergedCafe = cafeRepository.save(cafe);
		StudyOnce studyOnce = TestStudyOnceFactory.createStudyOnceWithTime(mergedCafe, mergedLeader, startDateTime,
			endDateTime);
		return studyOnceRepository.save(studyOnce);
	}
}
