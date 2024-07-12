package com.example.demo.repository.study;

import static com.example.demo.util.TruncatedTimeUtil.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.domain.study.QStudyOnce;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.dto.study.StudyOnceSearchRequest;
import com.example.demo.dto.study.TalkAbleState;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StudyOnceRepositoryImpl implements StudyOnceRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QStudyOnce qStudyOnce = QStudyOnce.studyOnce;

	@Override
	public List<StudyOnce> findAllByStudyOnceSearchRequest(StudyOnceSearchRequest studyOnceSearchRequest) {
		int maxMemberCount = studyOnceSearchRequest.getMaxMemberCount();
		TalkAbleState canTalk = studyOnceSearchRequest.getCanTalk();
		int sizePerPage = studyOnceSearchRequest.getSizePerPage();
		int page = studyOnceSearchRequest.getPage();
		String area = studyOnceSearchRequest.getArea();
		boolean onlyJoinAble = studyOnceSearchRequest.isOnlyJoinAble();
		return queryFactory.selectFrom(qStudyOnce)
			.where(talkAbleFilter(canTalk), maxMemberCountFilter(maxMemberCount), areaFilter(area),
				studyJoinAbleFilter(onlyJoinAble))
			.limit(sizePerPage)
			.offset((page - 1L) * sizePerPage)
			.fetch();
	}

	@Override
	public Long count(StudyOnceSearchRequest studyOnceSearchRequest) {
		int maxMemberCount = studyOnceSearchRequest.getMaxMemberCount();
		TalkAbleState canTalk = studyOnceSearchRequest.getCanTalk();
		String area = studyOnceSearchRequest.getArea();
		boolean onlyJoinAble = studyOnceSearchRequest.isOnlyJoinAble();
		Long count = queryFactory.select(qStudyOnce.count())
			.from(qStudyOnce)
			.where(talkAbleFilter(canTalk), maxMemberCountFilter(maxMemberCount), areaFilter(area),
				studyJoinAbleFilter(onlyJoinAble))
			.fetchFirst();
		if (count == null) {
			return 0L;
		}
		return count;
	}

	private BooleanExpression studyJoinAbleFilter(boolean onlyJoinAble) {
		LocalDateTime base = LOCAL_DATE_TIME_NOW.plusHours(3);
		if (onlyJoinAble) {
			return qStudyOnce.startDateTime.after(base)
				.or(qStudyOnce.startDateTime.eq(base));
		}
		return null;
	}

	private BooleanExpression areaFilter(String area) {
		return qStudyOnce.cafe.address.region.contains(area);
	}

	private BooleanExpression talkAbleFilter(TalkAbleState talkAbleState) {
		switch (talkAbleState) {
			case NO:
				return qStudyOnce.ableToTalk.eq(false);
			case YES:
				return qStudyOnce.ableToTalk.eq(true);
			case BOTH:
				return null;
			default:
				throw new IllegalStateException("잘못된 TalkAbleState");
		}

	}

	private BooleanExpression maxMemberCountFilter(int maxMemberCount) {
		if (maxMemberCount > 0) {
			return qStudyOnce.maxMemberCount.loe(maxMemberCount);
		}
		return null;
	}
}
