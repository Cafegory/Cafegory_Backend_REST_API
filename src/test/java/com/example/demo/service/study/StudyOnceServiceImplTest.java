package com.example.demo.service.study;

import static com.example.demo.domain.study.Attendance.*;
import static com.example.demo.exception.ExceptionType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.Attendance;
import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.member.MemberResponse;
import com.example.demo.dto.study.StudyMemberStateRequest;
import com.example.demo.dto.study.StudyMembersResponse;
import com.example.demo.dto.study.StudyOnceCreateRequest;
import com.example.demo.dto.study.StudyOnceCreateResponse;
import com.example.demo.dto.study.StudyOnceSearchListResponse;
import com.example.demo.dto.study.StudyOnceSearchRequest;
import com.example.demo.dto.study.StudyOnceSearchResponse;
import com.example.demo.dto.study.StudyOnceUpdateRequest;
import com.example.demo.dto.study.UpdateAttendanceRequest;
import com.example.demo.exception.CafegoryException;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.cafe.InMemoryCafeRepository;
import com.example.demo.repository.member.InMemoryMemberRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.study.InMemoryStudyMemberRepository;
import com.example.demo.repository.study.InMemoryStudyOnceRepository;
import com.example.demo.repository.study.StudyMemberRepository;
import com.example.demo.repository.study.StudyOnceRepository;
import com.example.demo.service.ServiceTest;

class StudyOnceServiceImplTest extends ServiceTest {
	private final MemberRepository memberRepository = InMemoryMemberRepository.INSTANCE;
	private final StudyOnceRepository studyOnceRepository = InMemoryStudyOnceRepository.INSTANCE;
	private final StudyMemberRepository studyMemberRepository = InMemoryStudyMemberRepository.INSTANCE;
	private final CafeRepository cafeRepository = InMemoryCafeRepository.INSTANCE;

	StudyOnceServiceImpl studyOnceService = new StudyOnceServiceImpl(cafeRepository, studyOnceRepository,
		memberRepository, studyMemberRepository, studyOnceMapper, studyMemberMapper);

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

	@Test
	@DisplayName("정상 목록 조회 테스트")
	void searchStudyByDto() {
		//given
		LocalDateTime start = LocalDateTime.now().plusHours(4).plusMinutes(1);
		LocalDateTime end = start.plusHours(1);
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		Member leader = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE);
		StudyOnceCreateResponse expectedStudyOnceSearchResponse = studyOnceService.createStudy(leader.getId(),
			makeStudyOnceCreateRequest(start, end, cafe.getId()));
		//when
		StudyOnceSearchRequest studyOnceSearchRequest = new StudyOnceSearchRequest("합정동");
		studyOnceSearchRequest.setMaxMemberCount(expectedStudyOnceSearchResponse.getMaxMemberCount());
		syncStudyOnceRepositoryAndStudyMemberRepository();
		PagedResponse<StudyOnceSearchListResponse> pagedResponse = studyOnceService.searchStudy(studyOnceSearchRequest);

		//then
		List<StudyOnceSearchListResponse> results = pagedResponse.getList();
		assertThat(results.size()).isEqualTo(1);
	}

	private StudyOnceCreateRequest makeStudyOnceCreateRequest(LocalDateTime start, LocalDateTime end,
		long cafeId) {
		return new StudyOnceCreateRequest(cafeId, "테스트 스터디", start, end, 4, true, "오픈채팅방 링크");
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
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);

		StudyOnceCreateResponse result = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		StudyOnceSearchResponse studyOnceSearchResponse = studyOnceService.searchByStudyId(result.getStudyOnceId());

		assertThat(studyOnceSearchResponse.getStudyOnceId()).isEqualTo(result.getStudyOnceId());
	}

	@Test
	@DisplayName("회원이 아닐떄 카공을 조회하면 카공의 참석여부는 false를 반환한다.")
	void searchByStudyId_when_not_member() {
		LocalDateTime start = LocalDateTime.now().plusHours(3).plusMinutes(1);
		LocalDateTime end = start.plusHours(1);
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceCreateResponse result = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);

		StudyOnceSearchResponse studyOnceSearchResponse = studyOnceService.searchByStudyId(result.getStudyOnceId());

		assertThat(studyOnceSearchResponse.isAttendance()).isFalse();
	}

	@Test
	@DisplayName("회원이 카공을 조회할때 카공 멤버라면 참석여부는 true를 반환한다.")
	void searchStudyOnceWithMemberParticipation_when_takes_attendance() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		studyOnceService.tryJoin(memberId, studyOnceId);
		syncStudyOnceRepositoryAndStudyMemberRepository();

		StudyOnceSearchResponse response = studyOnceService.searchStudyOnceWithMemberParticipation(
			studyOnceId, memberId);

		assertThat(response.isAttendance()).isTrue();
	}

	@Test
	@DisplayName("회원이 카공을 조회할때 카공 멤버가 아니라면 참석여부는 false를 반환한다.")
	void searchStudyOnceWithMemberParticipation_when_not_take_attendance() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();

		StudyOnceSearchResponse response = studyOnceService.searchStudyOnceWithMemberParticipation(
			studyOnceId, memberId);

		assertThat(response.isAttendance()).isFalse();
	}

	@Test
	@DisplayName("정상 생성 테스트")
	void create() {

		LocalDateTime start = LocalDateTime.now().plusHours(3).plusMinutes(1);
		LocalDateTime end = start.plusHours(1);
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);

		StudyOnceCreateResponse result = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		StudyOnceCreateResponse expected = makeExpectedStudyOnceCreateResult(cafeId, studyOnceCreateRequest, result);

		assertThat(result).isEqualTo(expected);
	}

	private static StudyOnceCreateResponse makeExpectedStudyOnceCreateResult(long cafeId,
		StudyOnceCreateRequest studyOnceCreateRequest, StudyOnceCreateResponse result) {
		int nowMemberCount = 1;
		boolean canJoin = true;
		boolean isEnd = false;
		return StudyOnceCreateResponse.builder()
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
			.openChatUrl(studyOnceCreateRequest.getOpenChatUrl())
			.build();
	}

	@Test
	@DisplayName("카공 시작 시간이 현재 시간 + 3시간 보다 이전인 경우 실패")
	void createFailByStartTime() {
		LocalDateTime start = LocalDateTime.now().plusHours(3).minusSeconds(1);
		LocalDateTime end = start.plusHours(3);
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();

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
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();

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
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();

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
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
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
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceCreateResponse study = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
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
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
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
		long firstMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long secondMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(LocalDateTime.now().plusHours(4),
			LocalDateTime.now().plusHours(7), cafeId);

		StudyOnceCreateResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);

		assertDoesNotThrow(() -> studyOnceService.tryJoin(secondMemberId, study.getStudyOnceId()));
	}

	@Test
	@DisplayName("이미 참여중인 카공이라 참여 실패")
	void tryJoinFailCauseDuplicate() {
		long firstMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long secondMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(LocalDateTime.now().plusHours(4),
			LocalDateTime.now().plusHours(7), cafeId);
		StudyOnceCreateResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);

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
		long firstMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long secondMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long thirdMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceCreateResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);
		studyOnceService.tryJoin(secondMemberId, study.getStudyOnceId());
		StudyOnceCreateRequest conflictStudyOnceCreateRequest = makeStudyOnceCreateRequest(conflictStart, conflictEnd,
			cafeId);

		StudyOnceCreateResponse conflictStudy = studyOnceService.createStudy(thirdMemberId,
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
		long firstMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long secondMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceCreateResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);
		studyOnceService.tryJoin(secondMemberId, study.getStudyOnceId());

		assertDoesNotThrow(() -> studyOnceService.tryQuit(secondMemberId, study.getStudyOnceId()));
	}

	@Test
	@DisplayName("참여중인 카공이 아니라 참여 취소 실패")
	void tryQuitFailCauseNotJoin() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long firstMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long secondMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);

		StudyOnceCreateResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);

		assertThatThrownBy(() -> studyOnceService.tryQuit(secondMemberId, study.getStudyOnceId()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_TRY_QUIT_NOT_JOIN.getErrorMessage());
	}

	@Test
	@DisplayName("다른 참여자가 있어서 카공장이 참여 취소 실패")
	void tryQuitFailCauseNotOnlyLeader() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long firstMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long secondMemberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceCreateResponse study = studyOnceService.createStudy(firstMemberId, studyOnceCreateRequest);

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
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long studyOnceId = searchResponse.getStudyOnceId();
		studyOnceService.tryJoin(memberId, studyOnceId);

		syncStudyOnceRepositoryAndStudyMemberRepository();
		LocalDateTime attendanceUpdateTime = start.plusMinutes(11);
		studyOnceService.updateAttendance(leaderId, studyOnceId, memberId, YES, attendanceUpdateTime);
		Member member = Member.builder().id(memberId).build();
		StudyMember studyMember = studyMemberRepository.findByMemberAndStudyDate(member, start.toLocalDate()).get(0);

		Attendance attendance = studyMember.getAttendance();
		assertEquals(YES, attendance);
	}

	@Test
	@DisplayName("참석여부를 업데이트 할때, 스터디가 존재하지 않으면 예외가 터진다.")
	void take_attendance_study_exception() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
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
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
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
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
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
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
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
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
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
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long studyOnceId = searchResponse.getStudyOnceId();
		studyOnceService.tryJoin(memberId, studyOnceId);
		long memberId2 = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		studyOnceService.tryJoin(memberId2, studyOnceId);

		syncStudyOnceRepositoryAndStudyMemberRepository();
		LocalDateTime attendanceUpdateTime = start.plusMinutes(11);
		UpdateAttendanceRequest request = new UpdateAttendanceRequest(
			new StudyMemberStateRequest(memberId, true),
			new StudyMemberStateRequest(memberId, true)
		);
		studyOnceService.updateAttendances(leaderId, studyOnceId, request, attendanceUpdateTime);
	}

	@Test
	@DisplayName("카공 장소를 변경할 수 있다.")
	void change_cafe() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();

		long changingCafeId = cafePersistHelper.persistDefaultCafe().getId();
		Long changedCafeId = studyOnceService.changeCafe(leaderId, studyOnceId, changingCafeId);
		assertThat(changedCafeId)
			.isEqualTo(changingCafeId);
	}

	@Test
	@DisplayName("카공장만이 카공 장소를 변경할 수 있다.")
	void change_cafe_by_leader_only() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();

		long changingCafeId = cafePersistHelper.persistDefaultCafe().getId();
		Long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		assertThatThrownBy(() -> studyOnceService.changeCafe(memberId, studyOnceId, changingCafeId))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LOCATION_CHANGE_PERMISSION_DENIED.getErrorMessage());
	}

	@Test
	@DisplayName("카공 참여자 정보 조회")
	void findStudyMembers() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		studyOnceService.tryJoin(memberId, studyOnceId);
		syncStudyOnceRepositoryAndStudyMemberRepository();

		StudyMembersResponse studyMembersById = studyOnceService.findStudyMembersById(studyOnceId);
		List<Long> actualStudyMemberIds = studyMembersById.getJoinedMembers().stream()
			.map(MemberResponse::getMemberId)
			.collect(Collectors.toList());

		assertThat(actualStudyMemberIds)
			.containsExactly(leaderId, memberId);
	}

	@Test
	@DisplayName("카공 수정")
	void updateStudyOnce() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId1 = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId1);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();
		long cafeId2 = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceUpdateRequest request = new StudyOnceUpdateRequest(cafeId2, "변경된카공이름", start.plusHours(5),
			start.plusHours(6), 5, false, "오픈채팅방 링크");

		studyOnceService.updateStudyOnce(leaderId, studyOnceId, request, LocalDateTime.now());
		StudyOnce studyOnce = studyOnceRepository.findById(studyOnceId).orElseThrow();

		assertAll(
			() -> assertThat(studyOnce.getName()).isEqualTo(request.getName()),
			() -> assertThat(studyOnce.getCafe().getId()).isEqualTo(request.getCafeId()),
			() -> assertThat(studyOnce.getStartDateTime()).isEqualTo(request.getStartDateTime()),
			() -> assertThat(studyOnce.getEndDateTime()).isEqualTo(request.getEndDateTime()),
			() -> assertThat(studyOnce.getMaxMemberCount()).isEqualTo(request.getMaxMemberCount()),
			() -> assertThat(studyOnce.isAbleToTalk()).isEqualTo(request.isCanTalk()),
			() -> assertThat(studyOnce.getOpenChatUrl()).isEqualTo(request.getOpenChatUrl())
		);
	}

	@Test
	@DisplayName("변경할 카페가 존재하지 않으면 예외가 터진다")
	void update_studyOnce_by_invalid_cafe_then_exception() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId1 = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId1);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();
		StudyOnceUpdateRequest request = new StudyOnceUpdateRequest(999L, "변경된카공이름", start.plusHours(5),
			start.plusHours(6), 5, false, "오픈채팅방 링크");

		assertThatThrownBy(() -> studyOnceService.updateStudyOnce(leaderId, studyOnceId, request, LocalDateTime.now()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(CAFE_NOT_FOUND.getErrorMessage());
	}

	@Test
	@DisplayName("스터디 리더가 아닌 다른 회원이 변경을 시도하면, 예외가 터진다")
	void update_studyOnce_by_invalid_member_then_exception() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId1 = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId1);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();
		StudyOnceUpdateRequest request = new StudyOnceUpdateRequest(cafeId1, "변경된카공이름", start.plusHours(5),
			start.plusHours(6), 5, false, "오픈채팅방 링크");
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();

		assertThatThrownBy(
			() -> studyOnceService.updateStudyOnce(memberId, studyOnceId, request, LocalDateTime.now()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LEADER_PERMISSION_DENIED.getErrorMessage());
	}

	@Test
	@DisplayName("참여자가 존재하는 경우, 카공 수정")
	void updateStudyOncePartially() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId1 = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId1);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long studyOnceId = searchResponse.getStudyOnceId();
		long cafeId2 = cafePersistHelper.persistDefaultCafe().getId();
		studyOnceService.tryJoin(memberId, studyOnceId);
		syncStudyOnceRepositoryAndStudyMemberRepository();

		StudyOnceUpdateRequest request = new StudyOnceUpdateRequest(cafeId2, null, null,
			null, 5, false, null);
		studyOnceService.updateStudyOncePartially(leaderId, studyOnceId, request, LocalDateTime.now());
		StudyOnce studyOnce = studyOnceRepository.findById(studyOnceId).orElseThrow();

		assertAll(
			() -> assertThat(studyOnce.getName()).isEqualTo(searchResponse.getName()),
			() -> assertThat(studyOnce.getCafe().getId()).isEqualTo(request.getCafeId()),
			() -> assertThat(studyOnce.getStartDateTime()).isEqualTo(searchResponse.getStartDateTime()),
			() -> assertThat(studyOnce.getEndDateTime()).isEqualTo(searchResponse.getEndDateTime()),
			() -> assertThat(studyOnce.getMaxMemberCount()).isEqualTo(request.getMaxMemberCount()),
			() -> assertThat(studyOnce.isAbleToTalk()).isEqualTo(searchResponse.isCanTalk()),
			() -> assertThat(studyOnce.getOpenChatUrl()).isEqualTo(searchResponse.getOpenChatUrl())
		);
	}

	@Test
	@DisplayName("참여자가 존재하는 경우, 카공을 수정할때 카공 장소와 최대 참여 인원 수만 수정된다.")
	void update_studyOnce_partially_then_update_partially() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId1 = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId1);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long studyOnceId = searchResponse.getStudyOnceId();
		long cafeId2 = cafePersistHelper.persistDefaultCafe().getId();
		studyOnceService.tryJoin(memberId, studyOnceId);
		syncStudyOnceRepositoryAndStudyMemberRepository();

		StudyOnceUpdateRequest request = new StudyOnceUpdateRequest(cafeId2, "변경된카공이름", start.plusHours(5),
			start.plusHours(6), 5, false, "오픈채팅방 링크");
		studyOnceService.updateStudyOncePartially(leaderId, studyOnceId, request, LocalDateTime.now());
		StudyOnce studyOnce = studyOnceRepository.findById(studyOnceId).orElseThrow();

		assertAll(
			() -> assertThat(studyOnce.getName()).isEqualTo(searchResponse.getName()),
			() -> assertThat(studyOnce.getCafe().getId()).isEqualTo(request.getCafeId()),
			() -> assertThat(studyOnce.getStartDateTime()).isEqualTo(searchResponse.getStartDateTime()),
			() -> assertThat(studyOnce.getEndDateTime()).isEqualTo(searchResponse.getEndDateTime()),
			() -> assertThat(studyOnce.getMaxMemberCount()).isEqualTo(request.getMaxMemberCount()),
			() -> assertThat(studyOnce.isAbleToTalk()).isEqualTo(searchResponse.isCanTalk()),
			() -> assertThat(studyOnce.getOpenChatUrl()).isEqualTo(searchResponse.getOpenChatUrl())
		);
	}

	@Test
	@DisplayName("스터디 리더가 아닌 다른 회원이 변경을 시도하면, 예외가 터진다")
	void update_studyOnce_partially_by_invalid_member_then_exception() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId1 = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId1);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();
		StudyOnceUpdateRequest request = new StudyOnceUpdateRequest(cafeId1, null, null,
			null, 5, true, null);
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();

		assertThatThrownBy(
			() -> studyOnceService.updateStudyOncePartially(memberId, studyOnceId, request, LocalDateTime.now()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LEADER_PERMISSION_DENIED.getErrorMessage());
	}

	@Test
	@DisplayName("카공에 카공장만 존재하는지 확인")
	void doesOnlyStudyLeaderExist() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId1 = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId1);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();

		boolean doesOnlyStudyLeaderExist = studyOnceService.doesOnlyStudyLeaderExist(studyOnceId);

		assertThat(doesOnlyStudyLeaderExist).isTrue();
	}

	@Test
	@DisplayName("카공에 카공장과 멤버가 존재하면 false 반환")
	void doesOnlyStudyLeaderExist_with_members() {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId1 = cafePersistHelper.persistDefaultCafe().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId1);
		long leaderId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		StudyOnceCreateResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		long memberId = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE).getId();
		long studyOnceId = searchResponse.getStudyOnceId();
		studyOnceService.tryJoin(memberId, studyOnceId);

		boolean doesOnlyStudyLeaderExist = studyOnceService.doesOnlyStudyLeaderExist(studyOnceId);

		assertThat(doesOnlyStudyLeaderExist).isFalse();
	}

}
