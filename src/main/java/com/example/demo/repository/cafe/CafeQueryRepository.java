package com.example.demo.repository.cafe;

import static com.example.demo.domain.cafe.QBusinessHour.*;
import static com.example.demo.domain.cafe.QCafe.*;
import static io.hypersistence.utils.hibernate.util.StringUtils.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.cafe.CafeSearchCondition;
import com.example.demo.domain.cafe.MaxAllowableStay;
import com.example.demo.domain.cafe.MinMenuPrice;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CafeQueryRepository {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	public List<Cafe> findWithDynamicFilterAndNoPaging(CafeSearchCondition searchCondition) {
		return queryFactory
			.selectFrom(cafe)
			.where(
				isAbleToStudy(searchCondition.isAbleToStudy()),
				regionContains(searchCondition.getRegion()),
				maxAllowableStayInLoe(searchCondition.getMaxAllowableStay()),
				minBeveragePriceLoe(searchCondition.getMinMenuPrice()),
				businessHourBetween(searchCondition.getStartTime(), searchCondition.getEndTime(),
					searchCondition.getNow())
			)
			.fetch();
	}

	public Page<Cafe> findWithDynamicFilter(CafeSearchCondition searchCondition, Pageable pageable) {
		List<Cafe> content = queryFactory
			.selectFrom(cafe)
			.where(
				isAbleToStudy(searchCondition.isAbleToStudy()),
				regionContains(searchCondition.getRegion()),
				maxAllowableStayInLoe(searchCondition.getMaxAllowableStay()),
				minBeveragePriceLoe(searchCondition.getMinMenuPrice()),
				businessHourBetween(searchCondition.getStartTime(), searchCondition.getEndTime(),
					searchCondition.getNow())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(cafe.count())
			.from(cafe)
			.where(
				isAbleToStudy(searchCondition.isAbleToStudy()),
				regionContains(searchCondition.getRegion()),
				maxAllowableStayInLoe(searchCondition.getMaxAllowableStay()),
				minBeveragePriceLoe(searchCondition.getMinMenuPrice()),
				businessHourBetween(searchCondition.getStartTime(), searchCondition.getEndTime(),
					searchCondition.getNow())
			);
		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	private BooleanExpression businessHourBetween(LocalTime filteringStartTime, LocalTime filteringEndTime,
		LocalDateTime now) {
		if (filteringStartTime == null || filteringEndTime == null || now == null) {
			return null;
		}
		String nowDayOfWeek = now.getDayOfWeek().toString();
		return cafe.id.in(
			JPAExpressions
				.select(businessHour.cafe.id)
				.from(businessHour)
				.where(
					businessHour.day.eq(nowDayOfWeek),
					businessHour.startTime.eq(filteringStartTime).or(businessHour.startTime.after(filteringStartTime)),
					businessHour.endTime.eq(filteringEndTime).or(businessHour.endTime.before(filteringEndTime))
				)
		);
	}

	private BooleanExpression minBeveragePriceLoe(MinMenuPrice minMenuPrice) {
		return minMenuPrice == null ? null : cafe.minBeveragePrice.loe(minMenuPrice.getRealValue());
	}

	//매개변수인 MaxAllowableStay보다 작거나 같은 MaxAllowableStay의 Enum상수가 in절안에 List로 들어감
	private BooleanExpression maxAllowableStayInLoe(MaxAllowableStay maxTime) {
		return maxTime == null
			? null : cafe.maxAllowableStay.in(MaxAllowableStay.findLoe(maxTime));
	}

	private BooleanExpression regionContains(String region) {
		return isBlank(region) ? null : cafe.address.region.contains(region);
	}

	private BooleanExpression isAbleToStudy(boolean isAbleToStudy) {
		return cafe.isAbleToStudy.eq(isAbleToStudy);
	}
}
