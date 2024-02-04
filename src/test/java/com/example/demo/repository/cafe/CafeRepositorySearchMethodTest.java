package com.example.demo.repository.cafe;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Address;
import com.example.demo.domain.BusinessHour;
import com.example.demo.domain.CafeImpl;
import com.example.demo.domain.MaxAllowableStay;
import com.example.demo.domain.MemberImpl;
import com.example.demo.domain.Menu;
import com.example.demo.domain.ReviewImpl;
import com.example.demo.domain.SnsDetail;
import com.example.demo.dto.CafeSearchCondition;
import com.example.demo.util.PageRequestCustom;

// @DataJpaTest
@SpringBootTest
@Profile("test")
@Transactional
	// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CafeRepositorySearchMethodTest {

	@Autowired
	private EntityManager em;
	@Autowired
	private CafeQueryRepository cafeRepository;

	void setUp(String region, MaxAllowableStay maxAllowableStay, boolean isAbleToStudy,
		int minBeveragePrice, LocalTime startTime, LocalTime endTime) {

		for (int i = 0; i < 20; i++) {
			CafeImpl cafe = CafeImpl.builder()
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

	void setUpWithMinBeveragePrice(CafeSearchCondition searchCondition, int minBeveragePrice) {

		for (int i = 0; i < 20; i++) {
			CafeImpl cafe = CafeImpl.builder()
				.name("카페고리" + i)
				.address(new Address("서울 마포구 " + searchCondition.getRegion(), searchCondition.getRegion()))
				.phone("010-1234-5678")
				.maxAllowableStay(searchCondition.getMaxAllowableStay())
				.isAbleToStudy(searchCondition.isAbleToStudy())
				.minBeveragePrice(minBeveragePrice)
				.build();
			em.persist(cafe);

			BusinessHour monday = BusinessHour.builder()
				.day("월")
				.startTime(LocalTime.of(9, 0))
				.endTime(LocalTime.of(21, 0))
				.cafe(cafe)
				.build();
			BusinessHour tuesday = BusinessHour.builder()
				.day("화")
				.startTime(LocalTime.of(9, 0))
				.endTime(LocalTime.of(21, 0))
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
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(
			createSearchConditionByRequirements(true, "상수동"));
		assertThat(cafes).isEqualTo(Collections.emptyList());
	}

	// @Test
	// @DisplayName("데이터가 존재, 필터링 없는 카페를 조회")
	// void search_Cafes_No_Filtering_When_Exists_Data_Then_Success() {
	// 	//given
	// 	CafeSearchCondition searchCondition = new CafeSearchCondition(true);
	// 	setUp(searchCondition);
	// 	//when
	// 	List<CafeImpl> cafes = cafeRepository.findWithDynamicFilter(searchCondition);
	// 	//then
	// 	assertThat(cafes.size()).isEqualTo(20);
	// }

	@Test
	@DisplayName("공부가 가능한 카페가 존재할때, 공부가 가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanStudy_When_Exists_CanStudyCafe() {
		//given

		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("공부가 가능한 카페가 존재할때, 공부가 불가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanNotStudy_When_Exists_CanStudyCafe() {
		//given
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(false, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("공부가 가능한 카페가 존재하지 않을때, 공부가 가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanStudy_When_Not_Exists_CanStudyCafe() {
		//given
		setUp("상수동", MaxAllowableStay.TWO_HOUR, false, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("공부가 가능한 카페가 존재하지 않을때, 공부가 불가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanNotStudy_When_Not_Exists_CanStudyCafe() {
		//given
		setUp("상수동", MaxAllowableStay.TWO_HOUR, false, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(false, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("공부가 가능한 카페와 불가능한 카페가 존재할때, 공부가 불가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanNotStudy_When_Exists_Both() {
		//given
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, false, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		CafeSearchCondition searchCondition = createSearchConditionByRequirements(false, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("공부가 가능한 카페와 불가능한 카페가 존재할때, 공부가 가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanStudy_When_Exists_Both() {
		//given
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, false, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("행정동으로 필터링, 조건에 맞는 데이터가 존재.")
	void search_Cafes_Filtering_With_Region() {
		//given
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("합정동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("일부 문자열만 입력된 행정동으로 필터링, 조건에 맞는 데이터가 존재.")
	void search_Cafes_Filtering_With_Like_Region() {
		//given
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("합정동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("존재하지 않는 행정동으로 필터링, 데이터가 존재하지 않음")
	void search_Cafes_Filtering_With_Invalid_Region_Then_NO_Data() {
		//given
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "쌍수100동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("whiteSpace, 공백문자, null인 행정동으로 필터링하면 필터링이 되지 않는다.")
	void search_Cafes_Filtering_With_Blank_Region_Then_No_Filtering() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("합정동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition1 = createSearchConditionByRequirements(true, null);
		//when
		List<CafeImpl> cafes1 = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition1);
		//then
		assertThat(cafes1.size()).isEqualTo(40);

		//given
		CafeSearchCondition searchCondition2 = createSearchConditionByRequirements(true, "");
		//when
		List<CafeImpl> cafes2 = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition2);
		//then
		assertThat(cafes2.size()).isEqualTo(40);

		//given
		CafeSearchCondition searchCondition3 = createSearchConditionByRequirements(true, " ");
		//when
		List<CafeImpl> cafes3 = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition3);
		//then
		assertThat(cafes3.size()).isEqualTo(40);
	}

	private CafeSearchCondition createSearchConditionByMaxTime(boolean isAbleToStudy, String region, int maxTime) {
		return new CafeSearchCondition.Builder(isAbleToStudy, region)
			.maxTime(maxTime)
			.build();
	}

	@Test
	@DisplayName("최대 이용 가능시간으로 필터링")
	void search_Cafes_Filtering_With_maxAllowableStay() {
		setUp("상수동", MaxAllowableStay.ONE_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.OVER_SIX_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition1 = createSearchConditionByMaxTime(true, "상수동", 1);
		//when
		List<CafeImpl> cafes1 = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition1);
		//then
		assertThat(cafes1.size()).isEqualTo(20);

		//given
		CafeSearchCondition searchCondition2 = createSearchConditionByMaxTime(true, "상수동", 2);
		//when
		List<CafeImpl> cafes2 = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition2);
		//then
		assertThat(cafes2.size()).isEqualTo(40);

		//given
		CafeSearchCondition searchCondition3 = createSearchConditionByMaxTime(true, "상수동", 0);
		//when
		List<CafeImpl> cafes3 = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition3);
		//then
		assertThat(cafes3.size()).isEqualTo(60);

		//given
		CafeSearchCondition searchCondition4 = createSearchConditionByMaxTime(true, "상수동", 7);
		//when
		List<CafeImpl> cafes4 = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition4);
		//then
		assertThat(cafes4.size()).isEqualTo(60);

		//given
		CafeSearchCondition searchCondition5 = createSearchConditionByMaxTime(true, "상수동", 6);
		//when
		List<CafeImpl> cafes5 = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition5);
		//then
		assertThat(cafes5.size()).isEqualTo(40);

	}

	private CafeSearchCondition createSearchConditionByMinMenuPrice(boolean isAbleToStudy, String region,
		int minMenuPrice) {
		return new CafeSearchCondition.Builder(isAbleToStudy, region)
			.minMenuPrice(minMenuPrice)
			.build();
	}

	@Test
	@DisplayName("최소 음료 금액으로 필터링")
	void search_Cafes_Filtering_With_MinMenuPrice() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 3_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 11_000, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 12_000, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//when
		CafeSearchCondition searchCondition1 = createSearchConditionByMinMenuPrice(true, "상수동", 3);
		List<CafeImpl> cafes1 = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition1);
		//then
		assertThat(cafes1.size()).isEqualTo(40);

		//when
		CafeSearchCondition searchCondition2 = createSearchConditionByMinMenuPrice(true, "상수동", 0);
		List<CafeImpl> cafes2 = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition2);
		//then
		assertThat(cafes2.size()).isEqualTo(80);

		//when
		CafeSearchCondition searchCondition3 = createSearchConditionByMinMenuPrice(true, "상수동", 10);
		List<CafeImpl> cafes3 = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition3);
		//then
		assertThat(cafes3.size()).isEqualTo(40);

		//when
		CafeSearchCondition searchCondition4 = createSearchConditionByMinMenuPrice(true, "상수동", 11);
		List<CafeImpl> cafes4 = cafeRepository.findWithDynamicFilterAndNoPaging(searchCondition4);
		//then
		assertThat(cafes4.size()).isEqualTo(80);

	}

	private CafeSearchCondition createSearchConditionByFilteringTime(boolean isAbleToStudy, String region,
		int filteringStartTime, int filteringEndTime, LocalDateTime now) {
		return new CafeSearchCondition.Builder(isAbleToStudy, region)
			.startTime(filteringStartTime)
			.endTime(filteringEndTime)
			.now(now)
			.build();
	}

	@Test
	@DisplayName("영업시간으로 필터링")
	void search_cafes_with_businessHours() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(10, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition cafeSearchCondition1 = createSearchConditionByFilteringTime(true, "상수동", 9, 21,
			LocalDateTime.of(2024, 1, 29, 8, 0));
		//when
		List<CafeImpl> cafes1 = cafeRepository.findWithDynamicFilterAndNoPaging(cafeSearchCondition1);
		//then
		assertThat(cafes1.size()).isEqualTo(40);

		//given
		CafeSearchCondition cafeSearchCondition2 = createSearchConditionByFilteringTime(true, "상수동", 0, 24,
			LocalDateTime.of(2024, 1, 29, 8, 0));
		//when
		List<CafeImpl> cafes2 = cafeRepository.findWithDynamicFilterAndNoPaging(cafeSearchCondition2);
		//then
		assertThat(cafes2.size()).isEqualTo(40);
	}

	@Test
	@DisplayName("영업시간이 24시간인 경우, 영업시간으로 필터링")
	void search_cafes_with_24hours_businessHours_() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(0, 0), LocalTime.MAX);
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(0, 0), LocalTime.MAX);
		//given
		CafeSearchCondition cafeSearchCondition1 = createSearchConditionByFilteringTime(true, "상수동", 0, 24,
			LocalDateTime.of(2024, 1, 29, 8, 0));
		//when
		List<CafeImpl> cafes1 = cafeRepository.findWithDynamicFilterAndNoPaging(cafeSearchCondition1);
		//then
		assertThat(cafes1.size()).isEqualTo(40);
	}

	@Test
	@DisplayName("영업종료시간이 새벽인 경우, 영업시간으로 필터링")
	void search_cafes_with_Overnight_businessHours_() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(2, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(2, 0));

		//given
		CafeSearchCondition cafeSearchCondition1 = createSearchConditionByFilteringTime(true, "상수동", 9, 2,
			LocalDateTime.of(2024, 1, 29, 8, 0));
		//when
		List<CafeImpl> cafes1 = cafeRepository.findWithDynamicFilterAndNoPaging(cafeSearchCondition1);
		//then
		assertThat(cafes1.size()).isEqualTo(40);

		//given
		CafeSearchCondition cafeSearchCondition2 = createSearchConditionByFilteringTime(true, "상수동", 9, 1,
			LocalDateTime.of(2024, 1, 29, 8, 0));
		//when
		List<CafeImpl> cafes2 = cafeRepository.findWithDynamicFilterAndNoPaging(cafeSearchCondition2);
		//then
		assertThat(cafes2.size()).isEqualTo(0);

		//given
		CafeSearchCondition cafeSearchCondition3 = createSearchConditionByFilteringTime(true, "상수동", 10, 2,
			LocalDateTime.of(2024, 1, 29, 8, 0));
		//when
		List<CafeImpl> cafes3 = cafeRepository.findWithDynamicFilterAndNoPaging(cafeSearchCondition3);
		//then
		assertThat(cafes3.size()).isEqualTo(0);

		//given
		CafeSearchCondition cafeSearchCondition4 = createSearchConditionByFilteringTime(true, "상수동", 0, 24,
			LocalDateTime.of(2024, 1, 29, 8, 0));
		//when
		List<CafeImpl> cafes4 = cafeRepository.findWithDynamicFilterAndNoPaging(cafeSearchCondition4);
		//then
		assertThat(cafes4.size()).isEqualTo(40);
	}

	@Test
	@DisplayName("페이징 기본값")
	void search_Cafes_With_Default_Paging() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		Page<CafeImpl> pagedCafes = cafeRepository.findWithDynamicFilter(searchCondition,
			PageRequestCustom.createByDefault());

		//then
		assertThat(pagedCafes.getContent().size()).isEqualTo(10);
		assertThat(pagedCafes.getTotalPages()).isEqualTo(2);
		assertThat(pagedCafes.getSize()).isEqualTo(10);
		assertThat(pagedCafes.getTotalElements()).isEqualTo(20);

	}

	@Test
	@DisplayName("페이징")
	void search_Cafes_With_Paging() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		Page<CafeImpl> pagedCafes1 = cafeRepository.findWithDynamicFilter(searchCondition,
			PageRequestCustom.of(1, 20));
		//then
		assertThat(pagedCafes1.getContent().size()).isEqualTo(20);

		//when
		Page<CafeImpl> pagedCafes2 = cafeRepository.findWithDynamicFilter(searchCondition,
			PageRequestCustom.of(1, 5));
		//then
		assertThat(pagedCafes2.getContent().size()).isEqualTo(5);

		//when
		Page<CafeImpl> pagedCafes3 = cafeRepository.findWithDynamicFilter(searchCondition,
			PageRequestCustom.of(2, 20));
		//then
		assertThat(pagedCafes3.getContent().size()).isEqualTo(20);

		//when
		Page<CafeImpl> pagedCafes4 = cafeRepository.findWithDynamicFilter(searchCondition,
			PageRequestCustom.of(3, 20));
		//then
		assertThat(pagedCafes4.getContent()).size().isEqualTo(0);

		//when
		Page<CafeImpl> pagedCafes5 = cafeRepository.findWithDynamicFilter(searchCondition,
			PageRequestCustom.of(1, 50));
		//then
		assertThat(pagedCafes5.getContent().size()).isEqualTo(40);

	}

	@Test
	void countQuery() {
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));
		setUp("상수동", MaxAllowableStay.TWO_HOUR, true, 2_500, LocalTime.of(9, 0), LocalTime.of(21, 0));

		//given
		CafeSearchCondition searchCondition = createSearchConditionByRequirements(true, "상수동");
		//when
		Page<CafeImpl> pagedCafes = cafeRepository.findWithDynamicFilter(searchCondition,
			PageRequestCustom.of(1, 20));
		//then
		assertThat(pagedCafes.getTotalElements()).isEqualTo(40);
	}

}