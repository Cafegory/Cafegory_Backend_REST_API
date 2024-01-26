package com.example.demo.repository.cafe;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Address;
import com.example.demo.domain.BusinessHour;
import com.example.demo.domain.CafeImpl;
import com.example.demo.domain.MemberImpl;
import com.example.demo.domain.Menu;
import com.example.demo.domain.ReviewImpl;
import com.example.demo.domain.SnsDetail;
import com.example.demo.service.dto.CafeSearchCondition;

@DataJpaTest
@Profile("test")
@Transactional
	// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CafeRepositorySearchMethodTest {

	@Autowired
	private EntityManager em;
	@Autowired
	private CafeRepository cafeRepository;

	void setUp(CafeSearchCondition searchCondition) {

		for (int i = 0; i < 20; i++) {
			CafeImpl cafe = CafeImpl.builder()
				.name("카페고리" + i)
				.address(new Address("서울 마포구 " + searchCondition.getRegion(), searchCondition.getRegion()))
				.phone("010-1234-5678")
				.maxAllowableStay(searchCondition.getMaxAllowableStay())
				.isAbleToStudy(searchCondition.isAbleToStudy())
				.build();
			em.persist(cafe);

			BusinessHour monday = new BusinessHour("월", LocalTime.of(9, 0), LocalTime.of(21, 0));
			cafe.addBusinessHour(monday);
			BusinessHour tuesday = new BusinessHour("화", LocalTime.of(9, 0), LocalTime.of(21, 0));
			cafe.addBusinessHour(tuesday);
			em.persist(monday);
			em.persist(tuesday);

			SnsDetail instagram = new SnsDetail("인스타그램", "https://www.instagram.com/cafegory/" + i);
			cafe.addSnsDetail(instagram);
			em.persist(instagram);

			MemberImpl member1 = new MemberImpl("김동현");
			em.persist(member1);
			MemberImpl member2 = new MemberImpl("임수빈");
			em.persist(member2);

			ReviewImpl review1 = new ReviewImpl("카페가 너무 이뻐요", 5, cafe, member1);
			cafe.addReview(review1);
			ReviewImpl review2 = new ReviewImpl("콘센트가 있어서 좋아요", 4.5, cafe, member2);
			cafe.addReview(review2);
			em.persist(review1);
			em.persist(review2);

			Menu menu1 = new Menu("아메리카노", 2000);
			cafe.addMenu(menu1);
			Menu menu2 = new Menu("카페라떼", 2500);
			cafe.addMenu(menu2);
			em.persist(menu1);
			em.persist(menu2);

		}

	}

	@Test
	@DisplayName("데이터가 없으면 빈값을 반환한다")
	void search_Cafes_When_No_Data_Then_EmptyList() {
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilter(new CafeSearchCondition(true, "상수동"));
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
		setUp(new CafeSearchCondition(true, "상수동"));
		CafeSearchCondition searchCondition = new CafeSearchCondition(true, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilter(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("공부가 가능한 카페가 존재할때, 공부가 불가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanNotStudy_When_Exists_CanStudyCafe() {
		//given
		setUp(new CafeSearchCondition(true, "상수동"));
		CafeSearchCondition searchCondition = new CafeSearchCondition(false, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilter(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("공부가 가능한 카페가 존재하지 않을때, 공부가 가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanStudy_When_Not_Exists_CanStudyCafe() {
		//given
		setUp(new CafeSearchCondition(false, "상수동"));
		CafeSearchCondition searchCondition = new CafeSearchCondition(true, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilter(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("공부가 가능한 카페가 존재하지 않을때, 공부가 불가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanNotStudy_When_Not_Exists_CanStudyCafe() {
		//given
		setUp(new CafeSearchCondition(false, "상수동"));
		CafeSearchCondition searchCondition = new CafeSearchCondition(false, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilter(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("공부가 가능한 카페와 불가능한 카페가 존재할때, 공부가 불가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanNotStudy_When_Exists_Both() {
		//given
		setUp(new CafeSearchCondition(true, "상수동"));
		setUp(new CafeSearchCondition(false, "상수동"));
		CafeSearchCondition searchCondition = new CafeSearchCondition(false, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilter(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("공부가 가능한 카페와 불가능한 카페가 존재할때, 공부가 가능한 카페로 필터링 조회")
	void search_Cafes_Filtering_With_CanStudy_When_Exists_Both() {
		//given
		setUp(new CafeSearchCondition(true, "상수동"));
		setUp(new CafeSearchCondition(false, "상수동"));
		CafeSearchCondition searchCondition = new CafeSearchCondition(true, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilter(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("행정동으로 필터링, 조건에 맞는 데이터가 존재.")
	void search_Cafes_Filtering_With_Region() {
		//given
		setUp(new CafeSearchCondition(true, "상수동"));
		setUp(new CafeSearchCondition(true, "합정동"));

		CafeSearchCondition searchCondition = new CafeSearchCondition(true, "상수동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilter(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("일부 문자열만 입력된 행정동으로 필터링, 조건에 맞는 데이터가 존재.")
	void search_Cafes_Filtering_With_Like_Region() {
		//given
		setUp(new CafeSearchCondition(true, "상수동"));
		setUp(new CafeSearchCondition(true, "합정동"));

		CafeSearchCondition searchCondition = new CafeSearchCondition(true, "상수");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilter(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(20);
	}

	@Test
	@DisplayName("존재하지 않는 행정동으로 필터링, 데이터가 존재하지 않음")
	void search_Cafes_Filtering_With_Invalid_Region_Then_NO_Data() {
		//given
		setUp(new CafeSearchCondition(true, "상수동"));

		CafeSearchCondition searchCondition = new CafeSearchCondition(true, "쌍수100동");
		//when
		List<CafeImpl> cafes = cafeRepository.findWithDynamicFilter(searchCondition);
		//then
		assertThat(cafes.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("whiteSpace, 공백문자, null인 행정동으로 필터링하면 필터링이 되지 않는다.")
	void search_Cafes_Filtering_With_Blank_Region_Then_No_Filtering() {
		setUp(new CafeSearchCondition(true, "상수동"));
		setUp(new CafeSearchCondition(true, "합정동"));

		//given
		CafeSearchCondition searchCondition1 = new CafeSearchCondition(true, null);
		//when
		List<CafeImpl> cafes1 = cafeRepository.findWithDynamicFilter(searchCondition1);
		//then
		assertThat(cafes1.size()).isEqualTo(40);

		//given
		CafeSearchCondition searchCondition2 = new CafeSearchCondition(true, "");
		//when
		List<CafeImpl> cafes2 = cafeRepository.findWithDynamicFilter(searchCondition2);
		//then
		assertThat(cafes2.size()).isEqualTo(40);

		//given
		CafeSearchCondition searchCondition3 = new CafeSearchCondition(true, " ");
		//when
		List<CafeImpl> cafes3 = cafeRepository.findWithDynamicFilter(searchCondition3);
		//then
		assertThat(cafes3.size()).isEqualTo(40);
	}

	@Test
	@DisplayName("최대 이용 가능시간으로 필터링")
	void search_Cafes_Filtering_With_maxAllowableStay() {
		setUp(new CafeSearchCondition(true, "상수동", 1));
		setUp(new CafeSearchCondition(true, "상수동", 2));
		setUp(new CafeSearchCondition(true, "상수동", 7));
		setUp(new CafeSearchCondition(true, "상수동", 0));

		//given
		CafeSearchCondition searchCondition1 = new CafeSearchCondition(true, "상수동", 1);
		//when
		List<CafeImpl> cafes1 = cafeRepository.findWithDynamicFilter(searchCondition1);
		//then
		assertThat(cafes1.size()).isEqualTo(40);

		//given
		CafeSearchCondition searchCondition2 = new CafeSearchCondition(true, "상수동", 2);
		//when
		List<CafeImpl> cafes2 = cafeRepository.findWithDynamicFilter(searchCondition2);
		//then
		assertThat(cafes2.size()).isEqualTo(60);

		//given
		CafeSearchCondition searchCondition3 = new CafeSearchCondition(true, "상수동", 0);
		//when
		List<CafeImpl> cafes3 = cafeRepository.findWithDynamicFilter(searchCondition3);
		//then
		assertThat(cafes3.size()).isEqualTo(80);

		//given
		CafeSearchCondition searchCondition4 = new CafeSearchCondition(true, "상수동", 7);
		//when
		List<CafeImpl> cafes4 = cafeRepository.findWithDynamicFilter(searchCondition4);
		//then
		assertThat(cafes4.size()).isEqualTo(80);

		//given
		CafeSearchCondition searchCondition5 = new CafeSearchCondition(true, "상수동", 6);
		//when
		List<CafeImpl> cafes5 = cafeRepository.findWithDynamicFilter(searchCondition5);
		//then
		assertThat(cafes5.size()).isEqualTo(60);

	}

}