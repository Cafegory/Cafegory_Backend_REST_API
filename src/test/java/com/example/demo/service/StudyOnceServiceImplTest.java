package com.example.demo.service;

import static com.example.demo.exception.ExceptionType.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.TestConfig;
import com.example.demo.domain.Address;
import com.example.demo.domain.Attendance;
import com.example.demo.domain.CafeImpl;
import com.example.demo.domain.MemberImpl;
import com.example.demo.domain.StudyMember;
import com.example.demo.domain.StudyMemberId;
import com.example.demo.domain.StudyOnceImpl;
import com.example.demo.domain.ThumbnailImage;
import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.StudyOnceCreateRequest;
import com.example.demo.dto.StudyOnceSearchRequest;
import com.example.demo.dto.StudyOnceSearchResponse;
import com.example.demo.exception.CafegoryException;
import com.example.demo.helper.CafePersistHelper;
import com.example.demo.helper.MemberPersistHelper;
import com.example.demo.helper.StudyMemberPersistHelper;
import com.example.demo.helper.StudyOncePersistHelper;
import com.example.demo.helper.ThumbnailImagePersistHelper;
import com.example.demo.repository.StudyMemberRepository;

@SpringBootTest
@Import(TestConfig.class)
@Transactional
class StudyOnceServiceImplTest {
	@Autowired
	StudyOnceServiceImpl studyOnceService;
	@Autowired
	StudyMemberRepository studyMemberRepository;
	@Autowired
	private EntityManager em;
	@Autowired
	private StudyMemberPersistHelper studyMemberPersistHelper;
	@Autowired
	private MemberPersistHelper memberPersistHelper;
	@Autowired
	private ThumbnailImagePersistHelper thumbnailImagePersistHelper;
	@Autowired
	private StudyOncePersistHelper studyOncePersistHelper;
	@Autowired
	private CafePersistHelper cafePersistHelper;

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

	@Test
	@DisplayName("정상 목록 조회 테스트")
	void searchStudyByDto() {
		//given
		LocalDateTime start = LocalDateTime.now().plusHours(4).plusMinutes(1);
		LocalDateTime end = start.plusHours(1);
		long cafeId = initCafe();
		long leaderId = initMember();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);

		//when
		StudyOnceSearchResponse expectedStudyOnceSearchResponse = studyOnceService.createStudy(leaderId,
			studyOnceCreateRequest);
		StudyOnceSearchRequest studyOnceSearchRequest = new StudyOnceSearchRequest("테스트동");
		PagedResponse<StudyOnceSearchResponse> pagedResponse = studyOnceService.searchStudy(studyOnceSearchRequest);

		//then
		List<StudyOnceSearchResponse> results = pagedResponse.getList();
		assertThat(results).contains(expectedStudyOnceSearchResponse);
	}

	@Test
	@DisplayName("정상 조회 테스트")
	void searchByStudyId() {
		LocalDateTime start = LocalDateTime.now().plusHours(3).plusMinutes(1);
		LocalDateTime end = start.plusHours(1);
		long cafeId = initCafe();
		long leaderId = initMember();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceSearchResponse result = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		StudyOnceSearchResponse studyOnceSearchResponse = studyOnceService.searchByStudyId(result.getId());
		assertThat(studyOnceSearchResponse.getId()).isEqualTo(result.getId());
	}

	@Test
	@DisplayName("정상 생성 테스트")
	void create() {
		LocalDateTime start = LocalDateTime.now().plusHours(3).plusMinutes(1);
		LocalDateTime end = start.plusHours(1);
		long cafeId = initCafe();
		long leaderId = initMember();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceSearchResponse result = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		StudyOnceSearchResponse expected = makeExpectedStudyOnceCreateResult(cafeId, studyOnceCreateRequest, result);
		assertThat(result).isEqualTo(expected);
	}

	private static StudyOnceCreateRequest makeStudyOnceCreateRequest(LocalDateTime start, LocalDateTime end,
		long cafeId) {
		return new StudyOnceCreateRequest(cafeId, "테스트 카페", start, end, 4, true);
	}

	@Test
	@DisplayName("카공 시작 시간이 현재 시간 + 3시간 보다 이전인 경우 실패")
	void createFailByStartTime() {
		LocalDateTime start = LocalDateTime.now().plusHours(3).minusSeconds(1);
		LocalDateTime end = start.plusHours(3);
		long cafeId = initCafe();
		long leaderId = initMember();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		assertThatThrownBy(() -> studyOnceService.createStudy(leaderId, studyOnceCreateRequest))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_WRONG_START_TIME.getErrorMessage());
	}

	@Test
	@DisplayName("카공 시작 시간과 종료시간 차이가 1시간 미만일 경우 실패")
	void createFailByStartTimeAndEndTimePeriodIsTooShort() {
		LocalDateTime start = LocalDateTime.now().plusHours(3).plusMinutes(1);
		LocalDateTime end = start.plusMinutes(59).plusSeconds(59);
		long cafeId = initCafe();
		long leaderId = initMember();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		assertThatThrownBy(() -> studyOnceService.createStudy(leaderId, studyOnceCreateRequest))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_SHORT_DURATION.getErrorMessage());
	}

	@Test
	@DisplayName("카공 시작 시간과 종료시간 차이가 5시간 초과일 경우 실패")
	void createFailByStartTimeAndEndTimePeriodIsTooLong() {
		LocalDateTime start = LocalDateTime.now().plusHours(3).plusMinutes(1);
		LocalDateTime end = start.plusHours(5).plusSeconds(1);
		long cafeId = initCafe();
		long leaderId = initMember();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		assertThatThrownBy(() -> studyOnceService.createStudy(leaderId, studyOnceCreateRequest))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LONG_DURATION.getErrorMessage());
	}

	private static StudyOnceSearchResponse makeExpectedStudyOnceCreateResult(long cafeId,
		StudyOnceCreateRequest studyOnceCreateRequest, StudyOnceSearchResponse result) {
		int nowMemberCount = 0;
		boolean canJoin = true;
		boolean isEnd = false;
		return StudyOnceSearchResponse.builder()
			.cafeId(cafeId)
			.id(result.getId())
			.name(studyOnceCreateRequest.getName())
			.startDateTime(studyOnceCreateRequest.getStartDateTime())
			.endDateTime(studyOnceCreateRequest.getEndDateTime())
			.maxMemberCount(studyOnceCreateRequest.getMaxMemberCount())
			.nowMemberCount(nowMemberCount)
			.canTalk(studyOnceCreateRequest.isCanTalk())
			.canJoin(canJoin)
			.isEnd(isEnd)
			.build();
	}

	@ParameterizedTest
	@MethodSource("createFailByAlreadyStudyLeaderParameters")
	@DisplayName("이미 해당 시간에 카공장으로 참여중인 카공이 있는 경우 카공 생성 실패")
	void createFailByAlreadyStudyLeader(LocalDateTime start, LocalDateTime end, LocalDateTime left,
		LocalDateTime right) {
		long cafeId = initCafe();
		long leaderId = initMember();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		studyOnceService.createStudy(leaderId, studyOnceCreateRequest);

		StudyOnceCreateRequest needToFailStudyOnceCreateRequest = makeStudyOnceCreateRequest(left, right, cafeId);
		assertThatThrownBy(() -> studyOnceService.createStudy(leaderId, needToFailStudyOnceCreateRequest))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CONFLICT_TIME.getErrorMessage());
	}

	static Stream<Arguments> createFailByAlreadyStudyLeaderParameters() {
		LocalDateTime start = LocalDateTime.now().plusYears(1);
		LocalDateTime end = start.plusHours(4);
		return Stream.of(Arguments.of(start, end, end, end.plusHours(4)),
			Arguments.of(start, end, start.minusHours(4), start));
	}

	@ParameterizedTest
	@MethodSource("createFailByAlreadyStudyLeaderParameters")
	@DisplayName("이미 해당 시간에 참여중인 카공이 있는 경우 카공 생성 실패")
	void createFailByAlreadyStudyMember(LocalDateTime start, LocalDateTime end, LocalDateTime left,
		LocalDateTime right) {
		long cafeId = initCafe();
		long leaderId = initMember();
		long memberId = initMember();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceSearchResponse study = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		studyOnceService.tryJoin(memberId, study.getId());
		// 오른쪽 끝에서 겹침
		StudyOnceCreateRequest needToFailStudyOnceCreateRequest = makeStudyOnceCreateRequest(left, right, cafeId);
		assertThatThrownBy(() -> studyOnceService.createStudy(memberId, needToFailStudyOnceCreateRequest))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CONFLICT_TIME.getErrorMessage());
	}

	@Test
	@DisplayName("1초 차이의 시간을 둔 카공은 생성 성공")
	void createTwo() {
		LocalDateTime start = LocalDateTime.now().plusYears(1).plusHours(3).plusMinutes(1);
		LocalDateTime end = start.plusHours(1);
		long cafeId = initCafe();
		long leaderId = initMember();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		// 오른쪽 끝에서 1초 차이로 안 겹침
		LocalDateTime rightLimitStart = end.plusSeconds(1);
		LocalDateTime rightLimitEnd = rightLimitStart.plusHours(1);
		StudyOnceCreateRequest needToFailStudyOnceCreateRequest = makeStudyOnceCreateRequest(rightLimitStart,
			rightLimitEnd, cafeId);
		studyOnceService.createStudy(leaderId, needToFailStudyOnceCreateRequest);

		// 왼쪽 끝에서 1초 차이로 안 겹침
		LocalDateTime leftLimitEnd = start.minusSeconds(1);
		LocalDateTime leftLimitStart = leftLimitEnd.minusHours(3);
		needToFailStudyOnceCreateRequest = makeStudyOnceCreateRequest(leftLimitStart,
			leftLimitEnd, cafeId);
		studyOnceService.createStudy(leaderId, needToFailStudyOnceCreateRequest);
	}

	@Test
	@DisplayName("카공 참여 테스트")
	void tryJoin() {
		long firstMemberId = initMember();
		long secondMemberId = initMember();
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(LocalDateTime.now().plusHours(4),
			LocalDateTime.now().plusHours(7), cafeId);
		StudyOnceSearchResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);
		studyOnceService.tryJoin(secondMemberId, study.getId());
	}

	@Test
	@DisplayName("이미 참여중인 카공이라 참여 실패")
	void tryJoinFailCauseDuplicate() {
		long firstMemberId = initMember();
		long secondMemberId = initMember();
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(LocalDateTime.now().plusHours(4),
			LocalDateTime.now().plusHours(7), cafeId);
		StudyOnceSearchResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);
		//when
		studyOnceService.tryJoin(secondMemberId, study.getId());
		//then
		assertThatThrownBy(() -> studyOnceService.tryJoin(secondMemberId, study.getId()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_DUPLICATE.getErrorMessage());
	}

	@ParameterizedTest
	@MethodSource("tryJoinFailCauseConflictParameters")
	@DisplayName("해당 시간에 이미 참여중인 카공이 있어서 참여 실패")
	void tryJoinFailCauseConflict(LocalDateTime start, LocalDateTime end, LocalDateTime conflictStart,
		LocalDateTime conflictEnd) {
		//given
		long firstMemberId = initMember();
		long secondMemberId = initMember();
		long thirdMemberId = initMember();
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceSearchResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);
		studyOnceService.tryJoin(secondMemberId, study.getId());
		//when
		StudyOnceCreateRequest conflictStudyOnceCreateRequest = makeStudyOnceCreateRequest(conflictStart, conflictEnd,
			cafeId);
		StudyOnceSearchResponse conflictStudy = studyOnceService.createStudy(thirdMemberId,
			conflictStudyOnceCreateRequest);
		//then
		assertThatThrownBy(() -> studyOnceService.tryJoin(secondMemberId, conflictStudy.getId()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CONFLICT_TIME.getErrorMessage());
	}

	static Stream<Arguments> tryJoinFailCauseConflictParameters() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		return Stream.of(Arguments.of(start, end, end.minusSeconds(1), end.minusSeconds(1).plusHours(4)),
			Arguments.of(start, end, start.plusSeconds(1), start.plusSeconds(1).plusHours(4)));
	}

	@Test
	@DisplayName("카공 참여 취소 테스트")
	void tryQuit() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long firstMemberId = initMember();
		long secondMemberId = initMember();
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceSearchResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);
		studyOnceService.tryJoin(secondMemberId, study.getId());
		studyOnceService.tryQuit(secondMemberId, study.getId());
	}

	@Test
	@DisplayName("참여중인 카공이 아니라 참여 취소 실패")
	void tryQuitFailCauseNotJoin() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long firstMemberId = initMember();
		long secondMemberId = initMember();
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceSearchResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);
		assertThatThrownBy(() -> studyOnceService.tryQuit(secondMemberId, study.getId()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_TRY_QUIT_NOT_JOIN.getErrorMessage());
	}

	@Test
	@DisplayName("다른 참여자가 있어서 카공장이 참여 취소 실패")
	void tryQuitFailCauseNotOnlyLeader() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long firstMemberId = initMember();
		long secondMemberId = initMember();
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceSearchResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);
		studyOnceService.tryJoin(secondMemberId, study.getId());
		assertThatThrownBy(() -> studyOnceService.tryQuit(firstMemberId, study.getId()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LEADER_QUIT_FAIL.getErrorMessage());
	}

	@Test
	@DisplayName("결석 업데이트")
	void take_attendance() {
		//given
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl leader = memberPersistHelper.persistMemberWithName(thumb, "김동현");
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnceImpl studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);

		MemberImpl member = memberPersistHelper.persistMemberWithName(thumb, "멤버");
		studyMemberPersistHelper.persistDefaultStudyMember(member, studyOnce);
		//when
		studyOnceService.updateAttendance(leader.getId(), studyOnce.getId(), member.getId(), Attendance.NO,
			LocalDateTime.of(2999, 2, 17, 18, 0), LocalDateTime.of(2999, 2, 17, 18, 10));
		em.flush();
		em.clear();
		StudyMember findMember = studyMemberRepository.findById(
			new StudyMemberId(member.getId(), studyOnce.getId())
		).get();
		//then
		assertThat(findMember.getAttendance()).isEqualTo(Attendance.NO);
	}

	@Test
	@DisplayName("참석여부를 업데이트 할때, 스터디가 존재하지 않으면 예외가 터진다.")
	void take_attendance_study_exception() {
		//given
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl leader = memberPersistHelper.persistMemberWithName(thumb, "김동현");
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnceImpl studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		MemberImpl member = memberPersistHelper.persistMemberWithName(thumb, "멤버");
		studyMemberPersistHelper.persistDefaultStudyMember(member, studyOnce);
		//then
		assertThatThrownBy(() ->
			studyOnceService.updateAttendance(leader.getId(), 10L, member.getId(), Attendance.NO,
				LocalDateTime.of(2999, 2, 17, 18, 0), LocalDateTime.of(2999, 2, 17, 18, 10))
		).isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_NOT_FOUND.getErrorMessage());
	}

	@Test
	@DisplayName("참석여부를 업데이트 할때, memberId가 틀리다면 예외가 터진다.")
	void take_attendance_memberId_exception() {
		//given
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl leader = memberPersistHelper.persistMemberWithName(thumb, "김동현");
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnceImpl studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		MemberImpl member = memberPersistHelper.persistMemberWithName(thumb, "멤버");
		studyMemberPersistHelper.persistDefaultStudyMember(member, studyOnce);
		//then
		assertThatThrownBy(() ->
			studyOnceService.updateAttendance(leader.getId(), studyOnce.getId(), 10L, Attendance.NO,
				LocalDateTime.of(2999, 2, 17, 18, 0), LocalDateTime.of(2999, 2, 17, 18, 10))
		).isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_MEMBER_NOT_FOUND.getErrorMessage());
	}

	@Test
	@DisplayName("참석여부를 업데이트 할때, 리더가 아니라면 예외가 터진다.")
	void take_attendance_leaderId_exception() {
		//given
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl leader = memberPersistHelper.persistMemberWithName(thumb, "김동현");
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnceImpl studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		MemberImpl member = memberPersistHelper.persistMemberWithName(thumb, "멤버");
		studyMemberPersistHelper.persistDefaultStudyMember(member, studyOnce);
		//then
		assertThatThrownBy(() ->
			studyOnceService.updateAttendance(10L, studyOnce.getId(), member.getId(), Attendance.NO,
				LocalDateTime.of(2999, 2, 17, 18, 0), LocalDateTime.of(2999, 2, 17, 18, 10))
		).isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_INVALID_LEADER.getErrorMessage());
	}

	@Test
	@DisplayName("카공이 시작된지 10분뒤 출석 체크를 할 수 있다.")
	void can_take_attendance_from_start_time_in_10min() {
		//given
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl leader = memberPersistHelper.persistMemberWithName(thumb, "김동현");
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnceImpl studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		MemberImpl member = memberPersistHelper.persistMemberWithName(thumb, "멤버");
		studyMemberPersistHelper.persistDefaultStudyMember(member, studyOnce);
		//then
		studyOnceService.updateAttendance(leader.getId(), studyOnce.getId(), member.getId(), Attendance.NO,
			LocalDateTime.of(2999, 2, 17, 18, 0), LocalDateTime.of(2999, 2, 17, 18, 10));
		em.flush();
		em.clear();
		StudyMember findMember = studyMemberRepository.findById(
			new StudyMemberId(member.getId(), studyOnce.getId())
		).get();
		//then
		assertThat(findMember.getAttendance()).isEqualTo(Attendance.NO);
	}

	@Test
	@DisplayName("카공이 시작된지 10분전이라면 예외가 터진다.")
	void can_take_attendance_from_start_time_in_10min_exception() {
		//given
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl leader = memberPersistHelper.persistMemberWithName(thumb, "김동현");
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnceImpl studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		MemberImpl member = memberPersistHelper.persistMemberWithName(thumb, "멤버");
		studyMemberPersistHelper.persistDefaultStudyMember(member, studyOnce);
		//then
		assertThatThrownBy(() ->
			studyOnceService.updateAttendance(leader.getId(), studyOnce.getId(), member.getId(), Attendance.NO,
				LocalDateTime.of(2999, 2, 17, 18, 0), LocalDateTime.of(2999, 2, 17, 18, 9, 59, 999_999_999))
		).isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_EARLY_TAKE_ATTENDANCE.getErrorMessage());
	}

	// @Test
	// @DisplayName("출석 체크는 스터디 진행시간이 절반이 지나기전에만 변경할 수 있다.")
	// void can_take_attendance_until_half_whole_study_time() {
	// 	//given
	// 	ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
	// 	MemberImpl leader = memberPersistHelper.persistMemberWithName(thumb, "김동현");
	// 	CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
	// 	StudyOnceImpl studyOnce = studyOncePersistHelper.persistStudyOnceWithTime(cafe, leader, LocalDateTime.of());
	// 	MemberImpl member = memberPersistHelper.persistMemberWithName(thumb, "멤버");
	// 	studyMemberPersistHelper.persistDefaultStudyMember(member, studyOnce);
	// 	//then
	// 	studyOnceService.updateAttendance(leader.getId(), studyOnce.getId(), member.getId(), Attendance.NO,
	// 		LocalDateTime.of(2999, 2, 17, 18, 0), LocalDateTime.of(2999, 2, 17, 18, 10));
	//
	// }

}
