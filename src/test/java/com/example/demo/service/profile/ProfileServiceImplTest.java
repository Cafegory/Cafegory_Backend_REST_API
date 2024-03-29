package com.example.demo.service.profile;

import static com.example.demo.exception.ExceptionType.*;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.TestConfig;
import com.example.demo.domain.Address;
import com.example.demo.domain.CafeImpl;
import com.example.demo.domain.MemberImpl;
import com.example.demo.domain.StudyOnceImpl;
import com.example.demo.domain.ThumbnailImage;
import com.example.demo.dto.StudyOnceCreateRequest;
import com.example.demo.dto.StudyOnceSearchResponse;
import com.example.demo.exception.CafegoryException;
import com.example.demo.service.StudyOnceService;

@SpringBootTest
@Import(TestConfig.class)
@Transactional
class ProfileServiceImplTest {
	@Autowired
	private ProfileService profileService;
	@Autowired
	private EntityManager em;
	@Autowired
	private StudyOnceService studyOnceService;

	private long initCafe() {
		Address address = new Address("테스트도 테스트시 테스트구 테스트동 ...", "테스트동");
		CafeImpl cafe = CafeImpl.builder()
			.address(address).build();
		em.persist(cafe);
		return cafe.getId();
	}

	private long initMember() {
		MemberImpl member = MemberImpl.builder()
			.name("테스트")
			.email("test@test.com")
			.thumbnailImage(ThumbnailImage.builder().thumbnailImage("testUrl").build())
			.build();
		em.persist(member);
		return member.getId();
	}

	private long initStudy(MemberImpl leader, CafeImpl cafe) {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		StudyOnceImpl studyOnce = StudyOnceImpl.builder()
			.leader(leader)
			.cafe(cafe)
			.startDateTime(start)
			.endDateTime(start.plusHours(5).minusMinutes(10))
			.ableToTalk(true)
			.maxMemberCount(5)
			.nowMemberCount(1)
			.isEnd(false)
			.build();
		em.persist(studyOnce);
		return studyOnce.getId();
	}

	@Test
	@DisplayName("자신이 스터디 장인 카공의 멤버면 프로필 조회 성공")
	void successWhenRequestMemberIsLeaderWithTargetMember() {
		long cafeId = initCafe();
		long requestMemberId = initMember();
		long targetMemberId = initMember();
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, start.plusHours(5), cafeId);
		StudyOnceSearchResponse study = studyOnceService.createStudy(requestMemberId, studyOnceCreateRequest);
		studyOnceService.tryJoin(targetMemberId, study.getStudyOnceId());
		Assertions.assertDoesNotThrow(() -> profileService.get(requestMemberId, targetMemberId));
	}

	@Test
	@DisplayName("자신이 참여 확정 상태인 카공의 멤버면 프로필 조회 성공")
	void successWhenRequestMemberAndTargetMemberJoinSameStudy() {
		long cafeId = initCafe();
		long requestMemberId = initMember();
		long targetMemberId = initMember();
		long studyLeaderId = initMember();
		CafeImpl cafe = em.find(CafeImpl.class, cafeId);
		MemberImpl leader = em.find(MemberImpl.class, studyLeaderId);
		long studyId = initStudy(leader, cafe);

		studyOnceService.tryJoin(targetMemberId, studyId);
		studyOnceService.tryJoin(requestMemberId, studyId);

		Assertions.assertDoesNotThrow(
			() -> profileService.get(requestMemberId, targetMemberId, LocalDateTime.now().plusHours(8)));
	}

	private static StudyOnceCreateRequest makeStudyOnceCreateRequest(LocalDateTime start, LocalDateTime end,
		long cafeId) {
		return new StudyOnceCreateRequest(cafeId, "테스트 카페", start, end, 4, true);
	}

	@Test
	@DisplayName("자신의 프로필 조회 성공")
	void successWhenRequestSelf() {
		long requestMemberId = initMember();
		Assertions.assertDoesNotThrow(
			() -> profileService.get(requestMemberId, requestMemberId));
	}

	@Test
	@DisplayName("프로필 조회 조건을 만족하지 않는 경우 실패")
	void failWhenOtherCase() {
		long requestMemberId = initMember();
		long targetMemberId = initMember();
		CafegoryException cafegoryException = Assertions.assertThrows(CafegoryException.class,
			() -> profileService.get(requestMemberId, targetMemberId));
		Assertions.assertEquals(cafegoryException.getMessage(), PROFILE_GET_PERMISSION_DENIED.getErrorMessage());
	}
}
