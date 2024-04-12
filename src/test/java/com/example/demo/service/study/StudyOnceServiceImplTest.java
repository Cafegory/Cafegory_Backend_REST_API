package com.example.demo.service.study;

import static com.example.demo.domain.study.Attendance.*;
import static com.example.demo.exception.ExceptionType.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.demo.domain.cafe.Address;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.study.Attendance;
import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.member.MemberResponse;
import com.example.demo.dto.study.StudyMemberStateRequest;
import com.example.demo.dto.study.StudyMembersResponse;
import com.example.demo.dto.study.StudyOnceCreateRequest;
import com.example.demo.dto.study.StudyOnceSearchRequest;
import com.example.demo.dto.study.StudyOnceSearchResponse;
import com.example.demo.dto.study.UpdateAttendanceRequest;
import com.example.demo.exception.CafegoryException;
import com.example.demo.mapper.StudyMemberMapper;
import com.example.demo.mapper.StudyOnceMapper;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.cafe.InMemoryCafeRepository;
import com.example.demo.repository.member.InMemoryMemberRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.study.InMemoryStudyMemberRepository;
import com.example.demo.repository.study.InMemoryStudyOnceRepository;
import com.example.demo.repository.study.StudyMemberRepository;
import com.example.demo.repository.study.StudyOnceRepository;

class StudyOnceServiceImplTest {
	private final MemberRepository memberRepository = new InMemoryMemberRepository();
	private final StudyOnceRepository studyOnceRepository = new InMemoryStudyOnceRepository();
	private final StudyMemberRepository studyMemberRepository = new InMemoryStudyMemberRepository();
	private final CafeRepository cafeRepository = new InMemoryCafeRepository();

	StudyOnceServiceImpl studyOnceService = new StudyOnceServiceImpl(cafeRepository, studyOnceRepository,
		memberRepository, studyMemberRepository, new StudyOnceMapper(), new StudyMemberMapper());

	static Stream<Arguments> createFailByAlreadyStudyLeaderParameters() {
		LocalDateTime start = LocalDateTime.now().plusYears(1);
		LocalDateTime end = start.plusHours(4);
		return Stream.of(Arguments.of(start, end, end, end.plusHours(4)),
			Arguments.of(start, end, start.minusHours(4), start));
	}

	static Stream<Arguments> tryJoinFailCauseConflictParameters() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		return Stream.of(Arguments.of(start, end, end.minusSeconds(1), end.minusSeconds(1).plusHours(4)),
			Arguments.of(start, end, start.plusSeconds(1), start.plusSeconds(1).plusHours(4)));
	}

	@BeforeEach
	void removeRepositories() {
		memberRepository.deleteAll();
		studyOnceRepository.deleteAll();
		studyMemberRepository.deleteAll();
		cafeRepository.deleteAll();
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
		studyOnceSearchRequest.setMaxMemberCount(studyOnceCreateRequest.getMaxMemberCount());
		syncStudyOnceRepositoryAndStudyMemberRepository();
		PagedResponse<StudyOnceSearchResponse> pagedResponse = studyOnceService.searchStudy(studyOnceSearchRequest);

		//then
		List<StudyOnceSearchResponse> results = pagedResponse.getList();
		assertThat(results).contains(expectedStudyOnceSearchResponse);
	}

	private long initCafe() {
		Address address = new Address("테스트도 테스트시 테스트구 테스트동 ...", "테스트동");
		Cafe cafe = Cafe.builder()
			.address(address)
			.name("테스트 카페")
			.build();
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

	private StudyOnceCreateRequest makeStudyOnceCreateRequest(LocalDateTime start, LocalDateTime end,
		long cafeId) {
		return new StudyOnceCreateRequest(cafeId, "테스트 스터디", start, end, 4, true);
	}

	private void syncStudyOnceRepositoryAndStudyMemberRepository() {
		List<StudyMember> allStudyMembers = studyOnceRepository.findAll().stream()
			.map(StudyOnce::getStudyMembers)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
		studyMemberRepository.saveAll(allStudyMembers);
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
		StudyOnceSearchResponse studyOnceSearchResponse = studyOnceService.searchByStudyId(result.getStudyOnceId());

		assertThat(studyOnceSearchResponse.getStudyOnceId()).isEqualTo(result.getStudyOnceId());
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

	private static StudyOnceSearchResponse makeExpectedStudyOnceCreateResult(long cafeId,
		StudyOnceCreateRequest studyOnceCreateRequest, StudyOnceSearchResponse result) {
		int nowMemberCount = 0;
		boolean canJoin = true;
		boolean isEnd = false;
		return StudyOnceSearchResponse.builder()
			.cafeId(cafeId)
			.area(result.getArea())
			.studyOnceId(result.getStudyOnceId())
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

		syncStudyOnceRepositoryAndStudyMemberRepository();
		assertThatThrownBy(() -> studyOnceService.createStudy(leaderId, needToFailStudyOnceCreateRequest))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CONFLICT_TIME.getErrorMessage());
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
		studyOnceService.tryJoin(memberId, study.getStudyOnceId());

		// 오른쪽 끝에서 겹침
		StudyOnceCreateRequest needToFailStudyOnceCreateRequest = makeStudyOnceCreateRequest(left, right, cafeId);

		syncStudyOnceRepositoryAndStudyMemberRepository();
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

		syncStudyOnceRepositoryAndStudyMemberRepository();
		studyOnceService.createStudy(leaderId, needToFailStudyOnceCreateRequest);

		// 왼쪽 끝에서 1초 차이로 안 겹침
		LocalDateTime leftLimitEnd = start.minusSeconds(1);
		LocalDateTime leftLimitStart = leftLimitEnd.minusHours(3);
		needToFailStudyOnceCreateRequest = makeStudyOnceCreateRequest(leftLimitStart,
			leftLimitEnd, cafeId);

		syncStudyOnceRepositoryAndStudyMemberRepository();
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

		Assertions.assertDoesNotThrow(() -> studyOnceService.tryJoin(secondMemberId, study.getStudyOnceId()));
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

		studyOnceService.tryJoin(secondMemberId, study.getStudyOnceId());

		syncStudyOnceRepositoryAndStudyMemberRepository();
		assertThatThrownBy(() -> studyOnceService.tryJoin(secondMemberId, study.getStudyOnceId()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_DUPLICATE.getErrorMessage());
	}

	@ParameterizedTest
	@MethodSource("tryJoinFailCauseConflictParameters")
	@DisplayName("해당 시간에 이미 참여중인 카공이 있어서 참여 실패")
	void tryJoinFailCauseConflict(LocalDateTime start, LocalDateTime end, LocalDateTime conflictStart,
		LocalDateTime conflictEnd) {
		long firstMemberId = initMember();
		long secondMemberId = initMember();
		long thirdMemberId = initMember();
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceSearchResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);
		studyOnceService.tryJoin(secondMemberId, study.getStudyOnceId());
		StudyOnceCreateRequest conflictStudyOnceCreateRequest = makeStudyOnceCreateRequest(conflictStart, conflictEnd,
			cafeId);

		StudyOnceSearchResponse conflictStudy = studyOnceService.createStudy(thirdMemberId,
			conflictStudyOnceCreateRequest);

		syncStudyOnceRepositoryAndStudyMemberRepository();
		assertThatThrownBy(() -> studyOnceService.tryJoin(secondMemberId, conflictStudy.getStudyOnceId()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CONFLICT_TIME.getErrorMessage());
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
		studyOnceService.tryJoin(secondMemberId, study.getStudyOnceId());

		Assertions.assertDoesNotThrow(() -> studyOnceService.tryQuit(secondMemberId, study.getStudyOnceId()));
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

		assertThatThrownBy(() -> studyOnceService.tryQuit(secondMemberId, study.getStudyOnceId()))
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

		studyOnceService.tryJoin(secondMemberId, study.getStudyOnceId());

		assertThatThrownBy(() -> studyOnceService.tryQuit(firstMemberId, study.getStudyOnceId()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LEADER_QUIT_FAIL.getErrorMessage());
	}

	@Test
	@DisplayName("출석 업데이트")
	void take_attendance() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = initMember();
		StudyOnceSearchResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = initMember();
		long studyOnceId = searchResponse.getStudyOnceId();
		studyOnceService.tryJoin(memberId, studyOnceId);

		syncStudyOnceRepositoryAndStudyMemberRepository();
		LocalDateTime attendanceUpdateTime = start.plusMinutes(11);
		studyOnceService.updateAttendance(leaderId, studyOnceId, memberId, YES, attendanceUpdateTime);
		Member member = Member.builder().id(memberId).build();
		StudyMember studyMember = studyMemberRepository.findByMemberAndStudyDate(member, start.toLocalDate()).get(0);

		Attendance attendance = studyMember.getAttendance();
		Assertions.assertEquals(YES, attendance);
	}

	@Test
	@DisplayName("참석여부를 업데이트 할때, 스터디가 존재하지 않으면 예외가 터진다.")
	void take_attendance_study_exception() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		long leaderId = initMember();
		long memberId = initMember();
		long studyOnceId = 1L;
		LocalDateTime attendanceUpdateTime = start.plusMinutes(11);
		assertThatThrownBy(
			() -> studyOnceService.updateAttendance(leaderId, studyOnceId, memberId, YES, attendanceUpdateTime))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_NOT_FOUND.getErrorMessage());
	}

	@Test
	@DisplayName("참석여부를 업데이트 할때, memberId가 틀리다면 예외가 터진다.")
	void take_attendance_memberId_exception() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = initMember();
		StudyOnceSearchResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = initMember();
		long studyOnceId = searchResponse.getStudyOnceId();
		studyOnceService.tryJoin(memberId, studyOnceId);

		syncStudyOnceRepositoryAndStudyMemberRepository();
		LocalDateTime attendanceUpdateTime = start.plusMinutes(11);
		assertThatThrownBy(
			() -> studyOnceService.updateAttendance(leaderId, studyOnceId, memberId + 1, YES, attendanceUpdateTime))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_MEMBER_NOT_FOUND.getErrorMessage());
	}

	@Test
	@DisplayName("참석여부를 업데이트 할때, 리더가 아니라면 예외가 터진다.")
	void take_attendance_leaderId_exception() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = initMember();
		StudyOnceSearchResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = initMember();
		long studyOnceId = searchResponse.getStudyOnceId();
		studyOnceService.tryJoin(memberId, studyOnceId);

		syncStudyOnceRepositoryAndStudyMemberRepository();
		LocalDateTime attendanceUpdateTime = start.plusMinutes(11);
		assertThatThrownBy(
			() -> studyOnceService.updateAttendance(memberId, studyOnceId, memberId, YES, attendanceUpdateTime))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_INVALID_LEADER.getErrorMessage());
	}

	@Test
	@DisplayName("카공이 시작된지 10분전이라면 예외가 터진다.")
	void can_take_attendance_from_start_time_in_10min_exception() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = initMember();
		StudyOnceSearchResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = initMember();
		long studyOnceId = searchResponse.getStudyOnceId();
		studyOnceService.tryJoin(memberId, studyOnceId);

		syncStudyOnceRepositoryAndStudyMemberRepository();
		LocalDateTime attendanceUpdateTime = start.plusMinutes(10).minusSeconds(1);
		assertThatThrownBy(
			() -> studyOnceService.updateAttendance(leaderId, studyOnceId, memberId, YES, attendanceUpdateTime))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_EARLY_TAKE_ATTENDANCE.getErrorMessage());
	}

	@Test
	@DisplayName("출석체크시 스터디 진행시간이 절반이 지난 후라면 예외가 터진다. ")
	void can_take_attendance_until_half_whole_study_time_exception() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = initMember();
		StudyOnceSearchResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = initMember();
		long studyOnceId = searchResponse.getStudyOnceId();
		studyOnceService.tryJoin(memberId, studyOnceId);

		syncStudyOnceRepositoryAndStudyMemberRepository();
		LocalDateTime attendanceUpdateTime = start.plusHours(2).plusSeconds(1);
		assertThatThrownBy(
			() -> studyOnceService.updateAttendance(leaderId, studyOnceId, memberId, YES, attendanceUpdateTime))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LATE_TAKE_ATTENDANCE.getErrorMessage());
	}

	@Test
	@DisplayName("출석 업데이트, 여러건")
	void can_take_attendances() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = initMember();
		StudyOnceSearchResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = initMember();
		long studyOnceId = searchResponse.getStudyOnceId();
		studyOnceService.tryJoin(memberId, studyOnceId);
		long memberId2 = initMember();
		studyOnceService.tryJoin(memberId2, studyOnceId);

		syncStudyOnceRepositoryAndStudyMemberRepository();
		LocalDateTime attendanceUpdateTime = start.plusMinutes(11);
		UpdateAttendanceRequest request = new UpdateAttendanceRequest(
			new StudyMemberStateRequest(memberId, true, attendanceUpdateTime),
			new StudyMemberStateRequest(memberId, true, attendanceUpdateTime)
		);
		studyOnceService.updateAttendances(leaderId, studyOnceId, request, attendanceUpdateTime);
	}

	@Test
	@DisplayName("카공 장소를 변경할 수 있다.")
	void change_cafe() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = initMember();
		StudyOnceSearchResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();

		long changingCafeId = initCafe();
		Long changedCafeId = studyOnceService.changeCafe(leaderId, studyOnceId, changingCafeId);
		assertThat(changedCafeId)
			.isEqualTo(changingCafeId);
	}

	@Test
	@DisplayName("카공장만이 카공 장소를 변경할 수 있다.")
	void change_cafe_by_leader_only() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = initMember();
		StudyOnceSearchResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();

		long changingCafeId = initCafe();
		assertThatThrownBy(() -> studyOnceService.changeCafe(initMember(), studyOnceId, changingCafeId))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LOCATION_CHANGE_PERMISSION_DENIED.getErrorMessage());
	}

	@Test
	@DisplayName("카공 참여자 정보 조회")
	void findStudyMembers() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = initMember();
		StudyOnceSearchResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();
		long memberId = initMember();
		studyOnceService.tryJoin(memberId, studyOnceId);
		syncStudyOnceRepositoryAndStudyMemberRepository();

		StudyMembersResponse studyMembersById = studyOnceService.findStudyMembersById(studyOnceId);
		List<Long> actualStudyMemberIds = studyMembersById.getJoinedMembers().stream()
			.map(MemberResponse::getMemberId)
			.collect(Collectors.toList());

		assertThat(actualStudyMemberIds)
			.containsExactly(leaderId, memberId);
	}
}
