package com.example.demo.service.profile;

import static com.example.demo.exception.ExceptionType.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.cafe.Address;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.dto.profile.ProfileResponse;
import com.example.demo.dto.profile.ProfileUpdateRequest;
import com.example.demo.dto.study.StudyOnceCreateRequest;
import com.example.demo.dto.study.StudyOnceSearchResponse;
import com.example.demo.exception.CafegoryException;
import com.example.demo.mapper.StudyMemberMapper;
import com.example.demo.mapper.StudyOnceMapper;
import com.example.demo.repository.cafe.InMemoryCafeRepository;
import com.example.demo.repository.member.InMemoryMemberRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.study.InMemoryStudyMemberRepository;
import com.example.demo.repository.study.InMemoryStudyOnceRepository;
import com.example.demo.repository.study.StudyMemberRepository;
import com.example.demo.repository.study.StudyOnceRepository;
import com.example.demo.service.study.StudyOnceService;
import com.example.demo.service.study.StudyOnceServiceImpl;

class ProfileServiceImplTest {
	private final StudyOnceRepository studyOnceRepository = new InMemoryStudyOnceRepository();
	private final MemberRepository memberRepository = new InMemoryMemberRepository();
	private final StudyMemberRepository studyMemberRepository = new InMemoryStudyMemberRepository();
	private final StudyOnceMapper studyOnceMapper = new StudyOnceMapper();
	private final StudyMemberMapper studyMemberMapper = new StudyMemberMapper();
	private final InMemoryCafeRepository cafeRepository = new InMemoryCafeRepository();

	private final ProfileService profileService = new ProfileServiceImpl(memberRepository, studyOnceRepository,
		studyMemberRepository);
	private final StudyOnceService studyOnceService = new StudyOnceServiceImpl(cafeRepository, studyOnceRepository,
		memberRepository, studyMemberRepository, studyOnceMapper, studyMemberMapper);

	private long initCafe() {
		Address address = new Address("테스트도 테스트시 테스트구 테스트동 ...", "테스트동");
		Cafe cafe = Cafe.builder()
			.address(address).build();
		cafe = cafeRepository.save(cafe);
		return cafe.getId();
	}

	private long initMember() {
		Member member = Member.builder()
			.name("테스트")
			.email("test@test.com")
			.thumbnailImage(ThumbnailImage.builder().thumbnailImage("testUrl").build())
			.build();
		member = memberRepository.save(member);
		return member.getId();
	}

	private long initStudy(Member leader, Cafe cafe) {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		StudyOnce studyOnce = StudyOnce.builder()
			.leader(leader)
			.cafe(cafe)
			.startDateTime(start)
			.endDateTime(start.plusHours(5).minusMinutes(10))
			.ableToTalk(true)
			.maxMemberCount(5)
			.nowMemberCount(1)
			.isEnd(false)
			.build();
		studyOnce = studyOnceRepository.save(studyOnce);
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
		Cafe cafe = cafeRepository.findById(cafeId).orElseThrow();
		Member leader = memberRepository.findById(studyLeaderId).orElseThrow();
		long studyId = initStudy(leader, cafe);

		studyOnceService.tryJoin(targetMemberId, studyId);
		studyOnceService.tryJoin(requestMemberId, studyId);

		syncStudyOnceRepositoryAndStudyMemberRepository();
		Assertions.assertDoesNotThrow(
			() -> profileService.get(requestMemberId, targetMemberId, LocalDateTime.now().plusHours(8)));
	}

	/**
	 * JPA 의 Cascade 가 InMemory 구현체에서는 작동하지 않으므로 수동 동기화가 필요함.
	 */
	private void syncStudyOnceRepositoryAndStudyMemberRepository() {
		List<StudyMember> allStudyMembers = studyOnceRepository.findAll().stream()
			.map(StudyOnce::getStudyMembers)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
		studyMemberRepository.saveAll(allStudyMembers);
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

	@Test
	@DisplayName("자신의 프로필을 수정하는 경우 성공")
	void updateSuccessWhenSelf() {
		long requestMemberId = initMember();
		ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest("name", "introduction");
		ProfileResponse update = profileService.update(requestMemberId, requestMemberId, profileUpdateRequest);
		Assertions.assertEquals(update, new ProfileResponse("name", "testUrl", "introduction"));
	}

	@Test
	@DisplayName("타인의 프로필을 수정하는 경우 실패")
	void updateFailWhenOther() {
		long requestMemberId = initMember();
		long targetMemberId = initMember();
		ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest("name", "introduction");
		CafegoryException cafegoryException = Assertions.assertThrows(CafegoryException.class,
			() -> profileService.update(requestMemberId, targetMemberId, profileUpdateRequest));
		Assertions.assertEquals(cafegoryException.getMessage(), PROFILE_UPDATE_PERMISSION_DENIED.getErrorMessage());
	}
}
