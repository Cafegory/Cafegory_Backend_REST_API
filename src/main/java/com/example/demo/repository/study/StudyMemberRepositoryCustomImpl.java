package com.example.demo.repository.study;

import static com.example.demo.domain.study.QStudyMember.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.StudyMember;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StudyMemberRepositoryCustomImpl implements StudyMemberRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<StudyMember> findByMemberAndStudyDate(Member member, LocalDate studyDate) {
		return queryFactory.selectFrom(studyMember)
			.where(studyMember.member.eq(member), makeStudyDateCondition(studyDate))
			.fetch();
	}

	private static BooleanExpression makeStudyDateCondition(LocalDate studyDate) {
		int year = studyDate.getYear();
		int monthValue = studyDate.getMonthValue();
		int dayOfMonth = studyDate.getDayOfMonth();

		return studyMember.study.startDateTime.year().eq(year)
			.and(studyMember.study.startDateTime.month().eq(monthValue))
			.and(studyMember.study.startDateTime.dayOfMonth().in(dayOfMonth, dayOfMonth - 1, dayOfMonth + 1));
	}
}
