package com.example.demo.repository.cafe;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.QueryDslConfig;
import com.example.demo.domain.cafe.Address;
import com.example.demo.domain.cafe.BusinessHour;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.cafe.CafeSearchCondition;
import com.example.demo.domain.cafe.MaxAllowableStay;
import com.example.demo.domain.cafe.Menu;
import com.example.demo.domain.cafe.SnsDetail;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.review.ReviewImpl;
import com.example.demo.util.PageRequestCustom;

// @SpringBootTest
@DataJpaTest
@Import({QueryDslConfig.class, CafeQueryRepository.class})
@Transactional
class CafeRepositorySearchMethodTest {

	@Autowired
	private EntityManager em;
	@Autowired
	private CafeQueryRepository cafeRepository;

	void setUp(String region, MaxAllowableStay maxAllowableStay, boolean isAbleToStudy,
		int minBeveragePrice, LocalTime startTime, LocalTime endTime) {

		for (int i = 0; i < 20; i++) {
			Cafe cafe = Cafe.builder()
				.name("카페고리" + i)
				.address(new Address("서울 마포구 " + region, region))
				.phone("010-1234-5678")
				.maxAllowableStay(maxAllowableStay)
				.isAbleToStudy(isAbleToStudy)
				.minBeveragePrice(minBeveragePrice)
				.build();
			em.persist(cafe);

			BusinessHour monday = BusinessHour.builder()
				.day("MONDAY")
				.startTime(startTime)
				.endTime(endTime)
				.cafe(cafe)
				.build();
			BusinessHour tuesday = BusinessHour.builder()
				.day("TUESDAY")
				.startTime(startTime)
				.endTime(endTime)
				.cafe(cafe)
				.build();
			em.persist(monday);
			em.persist(tuesday);

			SnsDetail instagram = SnsDetail.builder()
				.name("인스타그램")
				.url("https://www.instagram.com/cafegory/" + i)
				.cafe(cafe)
				.build();
			em.persist(instagram);

			MemberImpl member1 = MemberImpl.builder()
				.name("김동현")
				.build();
			MemberImpl member2 = MemberImpl.builder()
				.name("임수빈")
				.build();
			em.persist(member1);
			em.persist(member2);

			ReviewImpl review1 = ReviewImpl.builder()
				.content("카페가 너무 이뻐요")
				.rate(5)
				.cafe(cafe)
				.member(member1)
				.build();

			ReviewImpl review2 = ReviewImpl.builder()
				.content("콘센트가 있어서 좋아요")
				.rate(4.5)
				.cafe(cafe)
				.member(member2)
				.build();
			em.persist(review1);
			em.persist(review2);

			Menu menu1 = Menu.builder()
				.name("아메리카노")
				.price(2000)
				.cafe(cafe)
				.build();
			Menu menu2 = Menu.builder()
				.name("카페라떼")
				.price(2500)
				.cafe(cafe)
				.build();
			em.persist(menu1);
			em.persist(menu2);

		}

	}

	private CafeSearchCondition createSearchConditionByRequirements(boolean isAbleToStudy, String region) {
		return new CafeSearchCondition.Builder(isAbleToStudy, region)
			.build();
	}

	@Test
	@DisplayName("데이터가 없으면 빈값을 반환한다")
	void search_Cafes_When_No_Data_Then_EmptyList() {
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(
			createSearchConditionByRequirements(true, "상수동"));
		assertThat(cafes).isEqualTo(Collections.emptyList());
	}

	@Test
	@DisplayName("공부가 가능한 카페가 존재할때, 공부가 가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanStudy_When_Exists_CanStudyCafe() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("공부가 가능한 카페가 존재할때, 공부가 불가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanNotStudy_When_Exists_CanStudyCafe() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(false, "상수동");
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("공부가 가능한 카페가 존재하지 않을때, 공부가 가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanStudy_When_Not_Exists_CanStudyCafe() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, false, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("공부가 가능한 카페가 존재하지 않을때, 공부가 불가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanNotStudy_When_Not_Exists_CanStudyCafe() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, false, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(false, "상수동");
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("공부가 가능한 카페와 불가능한 카페가 존재할때, 공부가 불가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanNotStudy_When_Exists_Both() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, false, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(false, "상수동");
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("공부가 가능한 카페와 불가능한 카페가 존재할때, 공부가 가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanStudy_When_Exists_Both() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, false, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("행정동으로 필터링, 조건에 맞는 데이터가 존재.")
	void search_Cafes_Filtering_With_Region() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("합정동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("일부 문자열만 입력된 행정동으로 필터링, 조건에 맞는 데이터가 존재.")
	void search_Cafes_Filtering_With_Like_Region() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("합정동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수");
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("존재하지 않는 행정동으로 필터링, 데이터가 존재하지 않음")
	void search_Cafes_Filtering_With_Invalid_Region_Then_NO_Data() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "쌍수100동");
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(0);
	}

	@ParameterizedTest
	@MethodSource("provideConditionAndExpected")
	@DisplayName("whiteSpace, 공백문자, null인 행정동으로 필터링하면 필터링이 되지 않는다.")
	void search_Cafes_Filtering_With_Blank_Region_Then_No_Filtering(boolean isAbleToStudy, String region,
		int expected) {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("합정동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition1 = createSearchConditionByRequirements(isAbleToStudy, region);
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition1);
		//then
		assertThat(cafes.size()).isEqualTo(expected);
	}

	private static Stream<Arguments> provideConditionAndExpected() {
		return Stream.of(
			// boolean isAbleToStudy, String region, int expected
			Arguments.of(true, null, 40),
			Arguments.of(true, "", 40),
			Arguments.of(true, " ", 40)
		);
	}

	private CafeSearchCondition createSearchConditionByMaxTime(boolean isAbleToStudy, String region, int maxTime) {
		return new CafeSearchCondition.Builder(isAbleToStudy, region)
			.maxTime(maxTime)
			.build();
	}

	@ParameterizedTest
	@MethodSource("provideConditionByMaxTimeAndExpected")
	@DisplayName("최대 이용 가능시간으로 필터링")
	void search_Cafes_Filtering_With_maxAllowableStay(boolean isAbleToStudy, String region, int maxTime, int expected) {
		setUp("상수동", MaxAllowableStay.ONE_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.OVER_SIX_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByMaxTime(isAbleToStudy, region, maxTime);
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(expected);
	}

	private static Stream<Arguments> provideConditionByMaxTimeAndExpected() {
		return Stream.of(
			// boolean isAbleToStudy, String region, int maxTime, int expected
			Arguments.of(true, "상수동", 1, 20),
			Arguments.of(true, "상수동", 2, 40),
			Arguments.of(true, "상수동", 0, 60),
			Arguments.of(true, "상수동", 7, 60),
			Arguments.of(true, "상수동", 6, 40)
		);
	}

	private CafeSearchCondition createSearchConditionByMinMenuPrice(boolean isAbleToStudy, String region,
		int minMenuPrice) {
		return new CafeSearchCondition.Builder(isAbleToStudy, region)
			.minMenuPrice(minMenuPrice)
			.build();
	}

	@ParameterizedTest
	@MethodSource("provideConditionByMinMenuPriceAndExpected")
	@DisplayName("최소 음료 금액으로 필터링")
	void search_Cafes_Filtering_With_MinMenuPrice(boolean isAbleToStudy, String region, int minMenuPrice,
		int expected) {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 11_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 12_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//when
		CafeSearchCondition searchCondition = createSearchConditionByMinMenuPrice(isAbleToStudy, region, minMenuPrice);
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(expected);
	}

	private static Stream<Arguments> provideConditionByMinMenuPriceAndExpected() {
		return Stream.of(
			// boolean isAbleToStudy, String region, int minMenuPrice, int expected
			Arguments.of(true, "상수동", 3, 40),
			Arguments.of(true, "상수동", 0, 80),
			Arguments.of(true, "상수동", 10, 40),
			Arguments.of(true, "상수동", 11, 80)
		);
	}

	private CafeSearchCondition createSearchConditionByFilteringTime(boolean isAbleToStudy, String region,
		int filteringStartTime, int filteringEndTime, LocalDateTime now) {
		return new CafeSearchCondition.Builder(isAbleToStudy, region)
			.startTime(filteringStartTime)
			.endTime(filteringEndTime)
			.now(now)
			.build();
	}

	@ParameterizedTest
	@MethodSource("provideConditionByFilteringTimeAndExpected")
	@DisplayName("영업시간으로 필터링")
	void search_cafes_with_businessHours(boolean isAbleToStudy, String region, int filteringStartTime,
		int filteringEndTime, LocalDateTime now, int expected) {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(10, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition cafeSearchCondition = createSearchConditionByFilteringTime(isAbleToStudy, region,
			filteringStartTime, filteringEndTime, now);
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(cafeSearchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(expected);
	}

	private static Stream<Arguments> provideConditionByFilteringTimeAndExpected() {
		return Stream.of(
			// boolean isAbleToStudy, String region, int filteringStartTime, int filteringEndTime
			// LocalDateTime now
			// int expected
			Arguments.of(true, "상수동", 9, 21,
				LocalDateTime.of(2024, 1, 29, 8, 0),
				40
			),
			Arguments.of(true, "상수동", 0, 24,
				LocalDateTime.of(2024, 1, 29, 8, 0),
				40
			)
		);
	}

	@Test
	@DisplayName("영업시간이 24시간인 경우, 영업시간으로 필터링")
	void search_cafes_with_24hours_businessHours_() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(0, 0), LocalTime.MAX);
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(0, 0), LocalTime.MAX);

		//given
		CafeSearchCondition cafeSearchCondition = createSearchConditionByFilteringTime(true, "상수동", 0, 24,
			LocalDateTime.of(2024, 1, 29, 8, 0));
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(cafeSearchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(40);
	}

	@ParameterizedTest
	@MethodSource("provideConditionByFilteringTimeAndExpected2")
	@DisplayName("영업종료시간이 새벽인 경우, 영업시간으로 필터링")
	void search_cafes_with_Overnight_businessHours_(boolean isAbleToStudy, String region, int filteringStartTime,
		int filteringEndTime, LocalDateTime now, int expected) {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(2, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(2, 0));

		//given
		CafeSearchCondition cafeSearchCondition = createSearchConditionByFilteringTime(isAbleToStudy, region,
			filteringStartTime, filteringEndTime, now);
		//when
		List<Cafe> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(cafeSearchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(expected);
	}

	private static Stream<Arguments> provideConditionByFilteringTimeAndExpected2() {
		return Stream.of(
			// boolean isAbleToStudy, String region, int filteringStartTime, int filteringEndTime
			// LocalDateTime now
			// int expected
			Arguments.of(true, "상수동", 9, 2,
				LocalDateTime.of(2024, 1, 29, 8, 0),
				40
			),
			Arguments.of(true, "상수동", 9, 1,
				LocalDateTime.of(2024, 1, 29, 8, 0),
				0
			),
			Arguments.of(true, "상수동", 10, 2,
				LocalDateTime.of(2024, 1, 29, 8, 0),
				0
			),
			Arguments.of(true, "상수동", 0, 24,
				LocalDateTime.of(2024, 1, 29, 8, 0),
				40
			)
		);
	}

	@Test
	@DisplayName("페이징 기본값")
	void search_Cafes_With_Default_Paging() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		Page<Cafe> pagedCafes = cafeRepository.findWithDynamicFilter(searchCondition,
			PageRequestCustom.createByDefault());
		//then
		assertThat(pagedCafes.getContent().size()).isEqualTo(10);
		assertThat(pagedCafes.getTotalPages()).isEqualTo(2);
		assertThat(pagedCafes.getSize()).isEqualTo(10);
		assertThat(pagedCafes.getTotalElements()).isEqualTo(20);

	}

	@ParameterizedTest
	@MethodSource("providePagingAndExpected")
	@DisplayName("페이징")
	void search_Cafes_With_Paging(int page, int size, int expected) {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		Page<Cafe> pagedCafes = cafeRepository.findWithDynamicFilter(searchCondition,
			PageRequestCustom.of(page, size));
		//then
		assertThat(pagedCafes.getContent().size()).isEqualTo(expected);
	}

	private static Stream<Arguments> providePagingAndExpected() {
		return Stream.of(
			// int page, int size, int expected
			Arguments.of(1, 20, 20),
			Arguments.of(1, 5, 5),
			Arguments.of(2, 20, 20),
			Arguments.of(3, 20, 0),
			Arguments.of(1, 40, 10)
		);
	}

	@Test
	void countQuery() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		Page<Cafe> pagedCafes = cafeRepository.findWithDynamicFilter(searchCondition,
			PageRequestCustom.of(1, 20));
		//then
		assertThat(pagedCafes.getTotalElements()).isEqualTo(40);
	}

}
