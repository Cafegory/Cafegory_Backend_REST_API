package com.example.demo.service.study;

import static com.example.demo.domain.study.Attendance.*;
import static com.example.demo.exception.ExceptionType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyMemberId;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.dto.study.StudyMemberStateRequest;
import com.example.demo.dto.study.StudyMemberStateResponse;
import com.example.demo.dto.study.StudyOnceCreateRequest;
import com.example.demo.dto.study.StudyOnceCreateResponse;
import com.example.demo.dto.study.StudyOnceSearchResponse;
import com.example.demo.dto.study.StudyOnceUpdateRequest;
import com.example.demo.dto.study.UpdateAttendanceRequest;
import com.example.demo.dto.study.UpdateAttendanceResponse;
import com.example.demo.exception.CafegoryException;
import com.example.demo.helper.CafeSaveHelper;
import com.example.demo.helper.MemberSaveHelper;
import com.example.demo.helper.StudyOnceSaveHelper;
import com.example.demo.helper.ThumbnailImageSaveHelper;
import com.example.demo.repository.study.StudyMemberRepository;
import com.example.demo.repository.study.StudyOnceRepository;
import com.example.demo.service.ServiceTest;

class StudyOnceServiceImplTest extends ServiceTest {

	private static final LocalDateTime NOW = LocalDateTime.now();
	@Autowired
	private StudyOnceService sut;
	@Autowired
	private CafeSaveHelper cafeSaveHelper;
	@Autowired
	private MemberSaveHelper memberSaveHelper;
	@Autowired
	private StudyOnceRepository studyOnceRepository;
	@Autowired
	private StudyMemberRepository studyMemberRepository;
	@Autowired
	private ThumbnailImageSaveHelper thumbnailImageSaveHelper;
	@Autowired
	private StudyOnceSaveHelper studyOnceSaveHelper;

	@Test
	@DisplayName("정상 목록 조회 테스트")
	void searchStudyByDto() {

	}

	private StudyOnceCreateRequest makeStudyOnceCreateRequest(LocalDateTime start, LocalDateTime end,
		long cafeId) {
		return new StudyOnceCreateRequest(cafeId, "테스트 스터디", start, end, 4, true, "오픈채팅방 링크");
	}

	@Test
	@DisplayName("회원이 아닐떄 카공을 조회하면 카공의 참석여부는 false를 반환한다.")
	void searchByStudyId_when_not_member() {
		//TODO 테스트 코드, 프로덕션 코드 수정 필요
		LocalDateTime start = LocalDateTime.now().plusHours(3).plusMinutes(1);
		LocalDateTime end = start.plusHours(1);
		long cafeId = cafeSaveHelper.saveCafeWith24For7().getId();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		long leaderId = memberSaveHelper.saveMember(thumbnailImage).getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceCreateResponse result = sut.createStudy(leaderId, studyOnceCreateRequest);

		StudyOnceSearchResponse studyOnceSearchResponse = sut.searchByStudyId(result.getStudyOnceId());

		assertThat(studyOnceSearchResponse.isAttendance()).isFalse();
	}

	@Test
	@DisplayName("회원이 카공을 조회할때 카공 멤버라면 참석여부는 true를 반환한다.")
	void searchStudyOnceWithMemberParticipation_when_takes_attendance() {
		//TODO 테스트 코드, 프로덕션 코드 수정 필요
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = cafeSaveHelper.saveCafeWith24For7().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		long leaderId = memberSaveHelper.saveMember(thumbnailImage).getId();
		StudyOnceCreateResponse searchResponse = sut.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();
		long memberId = memberSaveHelper.saveMember(thumbnailImage).getId();
		sut.tryJoin(memberId, studyOnceId);

		StudyOnceSearchResponse response = sut.searchStudyOnceWithMemberParticipation(
			studyOnceId, memberId);

		assertThat(response.isAttendance()).isTrue();
	}

	@Test
	@DisplayName("회원이 카공을 조회할때 카공 멤버가 아니라면 참석여부는 false를 반환한다.")
	void searchStudyOnceWithMemberParticipation_when_not_take_attendance() {
		//TODO 테스트 코드, 프로덕션 코드 수정 필요
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = cafeSaveHelper.saveCafeWith24For7().getId();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		long leaderId = memberSaveHelper.saveMember(thumbnailImage).getId();
		StudyOnceCreateResponse searchResponse = sut.createStudy(leaderId, studyOnceCreateRequest);
		long studyOnceId = searchResponse.getStudyOnceId();
		long memberId = memberSaveHelper.saveMember(thumbnailImage).getId();

		StudyOnceSearchResponse response = sut.searchStudyOnceWithMemberParticipation(
			studyOnceId, memberId);

		assertThat(response.isAttendance()).isFalse();
	}

	@ParameterizedTest
	@MethodSource("provideLocalDateTime")
	@DisplayName("카공 시작시간이 23시이고 종료시간이 24시(23시 59분 59초)이면 카공이 생성된다.")
	void exception_case1(LocalDateTime end) {
		//given
		LocalDateTime start = LocalDateTime.of(2999, 1, 1, 23, 0);
		// LocalDateTime end = LocalDateTime.of(2999, 1, 1, 23, 59, 59);
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafeSaveHelper.saveCafeWith24For7();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafe.getId());
		//then
		assertDoesNotThrow(() -> sut.createStudy(leader.getId(), studyOnceCreateRequest));
	}

	static Stream<Arguments> provideLocalDateTime() {
		return Stream.of(
			Arguments.of(LocalDateTime.of(2999, 1, 1, 23, 59, 59))
			// 밑의 테스트 케이스는 마이그레이션 후 BusinessHour의 종료 시간이 999_999_000로 들어갈 때 성공한다.
			// Arguments.of(LocalDateTime.of(2999, 1, 1, 23, 59, 59, 999_999_000))
		);
	}

	@Test
	@DisplayName("카공 시작은 현재 시간으로부터 최소 3시간이후여야 한다.")
	void study_starts_3hours_after_now() {
		//TODO 추가 시간검증 필요
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		LocalDateTime start = NOW.plusHours(3).plusMinutes(1);
		LocalDateTime end = start.plusHours(1);
		Cafe cafe = cafeSaveHelper.saveCafeWith24For7();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafe.getId());
		//then
		assertDoesNotThrow(() -> sut.createStudy(leader.getId(), studyOnceCreateRequest));
	}

	@Test
	@DisplayName("카공 진행시간이 1시간 미만일 수 없다.")
	void study_duration_must_be_at_least_1hour() {
		//given
		LocalDateTime start = NOW.plusHours(4);
		LocalDateTime end = start.plusMinutes(59).plusSeconds(59).plusNanos(999_999_999);
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafeSaveHelper.saveCafeWith24For7();
		StudyOnceCreateRequest request = makeStudyOnceCreateRequest(start, end, cafe.getId());
		//then
		assertThatThrownBy(() -> sut.createStudy(leader.getId(), request))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_SHORT_DURATION.getErrorMessage());
	}

	@Test
	@DisplayName("카공 진행시간이 5시간 초과일 수 없다.")
	void study_duration_can_not_exceed_5hours() {
		//TODO LocalDateTime end = start.plusHours(5).plusNanos(1); 검증필요
		//given
		LocalDateTime start = NOW.plusHours(4);
		LocalDateTime end = start.plusHours(5).plusSeconds(1);
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafeSaveHelper.saveCafeWith24For7();
		StudyOnceCreateRequest request = makeStudyOnceCreateRequest(start, end, cafe.getId());
		//then
		assertThatThrownBy(() -> sut.createStudy(leader.getId(), request))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LONG_DURATION.getErrorMessage());
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 5})
	@DisplayName("카공 진행시간은 최소 1시간에서 최대 5시간까지 가능하다.")
	void check_study_duration_limits(int duration) {
		//given
		LocalDateTime start = NOW.plusHours(4);
		LocalDateTime end = start.plusHours(duration);
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafeSaveHelper.saveCafeWith24For7();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafe.getId());
		//then
		assertDoesNotThrow(() -> sut.createStudy(leader.getId(), studyOnceCreateRequest));
	}

	@ParameterizedTest
	@MethodSource("provideLocalDateTime1")
	@DisplayName("같은 시간대에 카공을 여러개 만들 수 없다.")
	void can_not_schedule_several_study_at_the_same_time(LocalDateTime testStartTime, LocalDateTime testEndTime) {
		//given
		LocalDateTime standard = NOW.plusYears(1);
		LocalDateTime start = standard.plusHours(4);
		LocalDateTime end = start.plusHours(5);

		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe1 = cafeSaveHelper.saveCafeWith24For7();
		Cafe cafe2 = cafeSaveHelper.saveCafeWith24For7();
		studyOnceSaveHelper.saveStudyOnceWithTime(cafe1, leader, start, end);
		StudyOnceCreateRequest request = makeStudyOnceCreateRequest(testStartTime, testEndTime,
			cafe2.getId());
		//then
		assertThatThrownBy(
			() -> sut.createStudy(leader.getId(), request))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CONFLICT_TIME.getErrorMessage());
	}

	static Stream<Arguments> provideLocalDateTime1() {
		LocalDateTime standard = NOW.plusYears(1);
		LocalDateTime start = standard.plusHours(4);
		return Stream.of(
			Arguments.of(start, start.plusHours(5)),
			Arguments.of(start.minusHours(2), start),
			Arguments.of(start.plusHours(5), start.plusHours(9))
		);
	}

	@ParameterizedTest
	@MethodSource("provideLocalDateTime2")
	@DisplayName("같은 시간대에 여러개의 카공에 참여할 수 없다.")
	void member_can_not_join_several_study_simultaneously(LocalDateTime testStartTime, LocalDateTime testEndTime) {
		//given
		LocalDateTime standard = NOW.plusYears(1);
		LocalDateTime start = standard.plusHours(4);
		LocalDateTime end = start.plusHours(5);

		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader1 = memberSaveHelper.saveMember(thumbnailImage);
		Member leader2 = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe1 = cafeSaveHelper.saveCafeWith24For7();
		Cafe cafe2 = cafeSaveHelper.saveCafeWith24For7();
		StudyOnce studyOnce1 = studyOnceSaveHelper.saveStudyOnceWithTime(cafe1, leader1, start, end);
		StudyOnce studyOnce2 = studyOnceSaveHelper.saveStudyOnceWithTime(cafe2, leader2, testStartTime, testEndTime);
		sut.tryJoin(member.getId(), studyOnce1.getId());
		//then
		assertThatThrownBy(() -> sut.tryJoin(member.getId(), studyOnce2.getId()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CONFLICT_TIME.getErrorMessage());
	}

	static Stream<Arguments> provideLocalDateTime2() {
		LocalDateTime standard = NOW.plusYears(1);
		LocalDateTime start = standard.plusHours(4);
		return Stream.of(
			Arguments.of(start, start.plusHours(5)),
			Arguments.of(start.minusHours(2), start),
			Arguments.of(start.plusHours(5), start.plusHours(9))
		);
	}

	@ParameterizedTest()
	@MethodSource("provideStartAndEndDateTime1")
	@DisplayName("카페 영업시간 밖의 시간에 카공을 만들 수 없다.")
	void study_can_not_start_outside_cafe_business_hours(LocalDateTime start, LocalDateTime end) {
		//given
		Cafe cafe = cafeSaveHelper.saveCafeWith7daysFrom9To21();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafe.getId());
		//then
		assertThatThrownBy(() -> sut.createStudy(leader.getId(), studyOnceCreateRequest))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CREATE_BETWEEN_CAFE_BUSINESS_HOURS.getErrorMessage());
	}

	static Stream<Arguments> provideStartAndEndDateTime1() {
		return Stream.of(
			Arguments.of(
				LocalDateTime.of(2999, 1, 1, 8, 59, 59, 999_999_999),
				LocalDateTime.of(2999, 1, 1, 10, 0)
			),
			Arguments.of(
				LocalDateTime.of(2999, 1, 1, 8, 0),
				LocalDateTime.of(2999, 1, 1, 9, 0, 0, 1)
			),
			Arguments.of(
				LocalDateTime.of(2999, 1, 1, 20, 0),
				LocalDateTime.of(2999, 1, 1, 21, 0, 1)
			),
			Arguments.of(
				LocalDateTime.of(2999, 1, 1, 20, 59, 59, 999_999_999),
				LocalDateTime.of(2999, 1, 1, 22, 0)
			)
		);
	}

	@ParameterizedTest()
	@MethodSource("provideStartAndEndDateTime2")
	@DisplayName("카페 영업시간 내의 시간에 카공을 만들 수 있다.")
	void study_can_start_between_cafe_business_hours(LocalDateTime start, LocalDateTime end) {
		//given
		Cafe cafe = cafeSaveHelper.saveCafeWith7daysFrom9To21();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafe.getId());
		//then
		assertDoesNotThrow(() -> sut.createStudy(leader.getId(), studyOnceCreateRequest));
	}

	static Stream<Arguments> provideStartAndEndDateTime2() {
		return Stream.of(
			Arguments.of(
				LocalDateTime.of(2999, 1, 1, 9, 0),
				LocalDateTime.of(2999, 1, 1, 10, 0)
			),
			Arguments.of(
				LocalDateTime.of(2999, 1, 1, 20, 0),
				LocalDateTime.of(2999, 1, 1, 21, 0)
			)
		);
	}

	@Test
	@DisplayName("카공이 시작하기전에 카공 참여를 취소할 수 있다.")
	void member_can_cancel_study_before_starts() {
		//given
		LocalDateTime start = NOW.plusHours(4);
		LocalDateTime end = start.plusHours(4);
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafeSaveHelper.saveCafeWith24For7();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe, leader, start, end);
		sut.tryJoin(member.getId(), studyOnce.getId());
		//when
		sut.tryQuit(member.getId(), studyOnce.getId());
		//then
		List<StudyMember> result = studyMemberRepository.findAll();
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("카공장은 카공 시작 10분 후 출석체크를 할 수 있다.")
	void leader_checks_attendance_10min_after_start() {
		//given
		LocalDateTime start = NOW.plusHours(4);
		LocalDateTime end = start.plusHours(4);
		Cafe cafe = cafeSaveHelper.saveCafeWith24For7();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe, leader, start, end);
		sut.tryJoin(member.getId(), studyOnce.getId());
		LocalDateTime attendanceUpdateTime = start.plusMinutes(10);
		//when
		sut.updateAttendance(leader.getId(), studyOnce.getId(), member.getId(), NO, attendanceUpdateTime);
		//then
		StudyMember studyMember = studyMemberRepository.findById(
			new StudyMemberId(member.getId(), studyOnce.getId())
		).get();
		assertThat(studyMember.getAttendance()).isEqualTo(NO);
	}

	@Test
	@DisplayName("카공 시작 10분 전에 출석체크를 할 수 없다.")
	void leader_can_not_check_attendance_10min_before_start() {
		//given
		LocalDateTime start = NOW.plusHours(4);
		LocalDateTime end = start.plusHours(4);
		Cafe cafe = cafeSaveHelper.saveCafeWith24For7();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe, leader, start, end);
		sut.tryJoin(member.getId(), studyOnce.getId());
		LocalDateTime attendanceUpdateTime = start.plusMinutes(10).minusSeconds(1);
		//when
		assertThatThrownBy(
			() -> sut.updateAttendance(leader.getId(), studyOnce.getId(), member.getId(), NO, attendanceUpdateTime))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_EARLY_TAKE_ATTENDANCE.getErrorMessage());
	}

	@Test
	@DisplayName("카공장은 카공 진행시간 절반이 지나기 전까지 출석체크를 할 수 있다.")
	void leader_can_check_attendance_until_half_whole_study_time() {
		//given
		LocalDateTime start = NOW.plusHours(4);
		LocalDateTime end = start.plusHours(4);
		Cafe cafe = cafeSaveHelper.saveCafeWith24For7();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe, leader, start, end);
		sut.tryJoin(member.getId(), studyOnce.getId());
		LocalDateTime attendanceUpdateTime = start.plusHours(2);
		//when
		sut.updateAttendance(leader.getId(), studyOnce.getId(), member.getId(), NO, attendanceUpdateTime);
		//then
		StudyMember studyMember = studyMemberRepository.findById(
			new StudyMemberId(member.getId(), studyOnce.getId())
		).get();
		assertThat(studyMember.getAttendance()).isEqualTo(NO);
	}

	@Test
	@DisplayName("카공 진행시간 절반이 지나면 출석체크를 할 수 없다.")
	void leader_can_not_check_attendance_after_half_whole_study_time() {
		//given
		LocalDateTime start = NOW.plusHours(4);
		LocalDateTime end = start.plusHours(4);
		Cafe cafe = cafeSaveHelper.saveCafeWith24For7();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe, leader, start, end);
		sut.tryJoin(member.getId(), studyOnce.getId());
		LocalDateTime attendanceUpdateTime = start.plusHours(2).plusSeconds(1);
		//when
		assertThatThrownBy(
			() -> sut.updateAttendance(leader.getId(), studyOnce.getId(), member.getId(), NO, attendanceUpdateTime))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LATE_TAKE_ATTENDANCE.getErrorMessage());
	}

	@Test
	@DisplayName("여러명 출석체크를 한다.")
	void check_attendances() {
		//given
		LocalDateTime start = NOW.plusHours(4);
		LocalDateTime end = start.plusHours(4);
		Cafe cafe = cafeSaveHelper.saveCafeWith24For7();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member1 = memberSaveHelper.saveMember(thumbnailImage);
		Member member2 = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe, leader, start, end);
		sut.tryJoin(member1.getId(), studyOnce.getId());
		sut.tryJoin(member2.getId(), studyOnce.getId());
		LocalDateTime attendanceUpdateTime = start.plusMinutes(11);
		UpdateAttendanceRequest request = new UpdateAttendanceRequest(
			new StudyMemberStateRequest(member1.getId(), false),
			new StudyMemberStateRequest(member2.getId(), false)
		);
		//when
		UpdateAttendanceResponse response = sut.updateAttendances(leader.getId(), studyOnce.getId(),
			request, attendanceUpdateTime);
		//then
		List<StudyMemberStateResponse> result = response.getStates();
		assertAll(
			() -> assertThat(result.get(0).isAttendance()).isEqualTo(false),
			() -> assertThat(result.get(1).isAttendance()).isEqualTo(false)
		);
	}

	@Test
	@DisplayName("카공장만이 카공 장소를 변경할 수 있다.")
	void only_leader_changes_cafe() {
		//given
		LocalDateTime start = NOW.plusHours(4);
		LocalDateTime end = start.plusHours(4);
		Cafe cafe1 = cafeSaveHelper.saveCafeWith24For7();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe1, leader, start, end);
		Cafe cafe2 = cafeSaveHelper.saveCafeWith24For7();
		//when
		Long changedCafeId = sut.changeCafe(leader.getId(), studyOnce.getId(), cafe2.getId());
		//then
		assertThat(changedCafeId).isEqualTo(cafe2.getId());
	}

	@Test
	@DisplayName("카공장만이 카공을 수정할 수 있다.")
	void only_leader_updates_study_details() {
		//given
		LocalDateTime start = NOW.plusHours(4);
		LocalDateTime end = start.plusHours(4);
		Cafe cafe1 = cafeSaveHelper.saveCafeWith24For7();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe1, leader, start, end);
		Cafe cafe2 = cafeSaveHelper.saveCafeWith24For7();
		StudyOnceUpdateRequest request = new StudyOnceUpdateRequest(cafe2.getId(), "변경된카공이름", start.plusHours(5),
			start.plusHours(6), 5, false, "오픈채팅방 링크");
		//then
		assertDoesNotThrow(() -> sut.updateStudyOnce(leader.getId(), studyOnce.getId(), request, LocalDateTime.now()));

	}

	@Test
	@DisplayName("카공장이 아니라면 카공을 수정할 수 없다.")
	void non_leader_can_not_update_study_details() {
		//given
		LocalDateTime start = NOW.plusHours(4);
		LocalDateTime end = start.plusHours(4);
		Cafe cafe1 = cafeSaveHelper.saveCafeWith24For7();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe1, leader, start, end);
		Cafe cafe2 = cafeSaveHelper.saveCafeWith24For7();
		StudyOnceUpdateRequest request = new StudyOnceUpdateRequest(cafe2.getId(), "변경된카공이름", start.plusHours(5),
			start.plusHours(6), 5, false, "오픈채팅방 링크");
		//then
		assertThatThrownBy(() -> sut.updateStudyOnce(member.getId(), studyOnce.getId(), request, LocalDateTime.now()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LEADER_PERMISSION_DENIED.getErrorMessage());
	}

	@ParameterizedTest
	@MethodSource("provideStartAndEndDateTime3")
	@DisplayName("카공 시간 변경시 카공 시간은 카페 영업시간내에 포함되어야 한다.")
	void study_time_must_be_within_cafe_business_hours(LocalDateTime start, LocalDateTime end) {
		//given
		Cafe cafe = cafeSaveHelper.saveCafeWith7daysFrom9To21();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe, leader,
			LocalDateTime.of(2999, 1, 1, 9, 0),
			LocalDateTime.of(2999, 1, 1, 10, 0));
		StudyOnceUpdateRequest request = new StudyOnceUpdateRequest(cafe.getId(), null, start, end, 5,
			true, null);
		//then
		assertThatThrownBy(
			() -> sut.updateStudyOnce(leader.getId(), studyOnce.getId(),
				request, LocalDateTime.of(2999, 1, 1, 8, 0)))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CREATE_BETWEEN_CAFE_BUSINESS_HOURS.getErrorMessage());
	}

	static Stream<Arguments> provideStartAndEndDateTime3() {
		return Stream.of(
			Arguments.of(
				LocalDateTime.of(2999, 1, 1, 8, 59, 59, 999_999_999),
				LocalDateTime.of(2999, 1, 1, 10, 0)
			),
			Arguments.of(
				LocalDateTime.of(2999, 1, 1, 8, 0),
				LocalDateTime.of(2999, 1, 1, 9, 0, 0, 100_000_000)
			),
			Arguments.of(
				LocalDateTime.of(2999, 1, 1, 20, 0),
				LocalDateTime.of(2999, 1, 1, 21, 0, 1)
			),
			Arguments.of(
				LocalDateTime.of(2999, 1, 1, 20, 59, 59, 999_999_999),
				LocalDateTime.of(2999, 1, 1, 22, 0)
			)
		);
	}

	@ParameterizedTest()
	@MethodSource("provideStartAndEndDateTime4")
	@DisplayName("카공 시간 변경시 카공 시간은 카페 영업시간내에 포함된다.")
	void study_time_is_within_cafe_business_hours(LocalDateTime start, LocalDateTime end) {
		//given
		Cafe cafe = cafeSaveHelper.saveCafeWith7daysFrom9To21();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe, leader,
			LocalDateTime.of(2999, 1, 1, 9, 0),
			LocalDateTime.of(2999, 1, 1, 10, 0));
		StudyOnceUpdateRequest request = new StudyOnceUpdateRequest(cafe.getId(), null, start, end, 5,
			true, null);
		//then
		assertDoesNotThrow(
			() -> sut.updateStudyOnce(leader.getId(), studyOnce.getId(),
				request, LocalDateTime.of(2999, 1, 1, 8, 0)));
	}

	static Stream<Arguments> provideStartAndEndDateTime4() {
		return Stream.of(
			Arguments.of(
				LocalDateTime.of(2999, 1, 1, 9, 0),
				LocalDateTime.of(2999, 1, 1, 10, 0)
			),
			Arguments.of(
				LocalDateTime.of(2999, 1, 1, 20, 0),
				LocalDateTime.of(2999, 1, 1, 21, 0)
			)
		);
	}

	@Test
	@DisplayName("참여자가 존재하는 경우, 카공을 수정할때 카공 장소와 최대 참여 인원 수만 수정된다.")
	void update_location_and_count_with_participants() {
		//given
		LocalDateTime start = NOW.plusHours(4);
		Cafe cafe1 = cafeSaveHelper.saveCafe();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnce(cafe1, leader);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		long cafeId2 = cafeSaveHelper.saveCafe().getId();
		sut.tryJoin(member.getId(), studyOnce.getId());
		StudyOnceUpdateRequest request = new StudyOnceUpdateRequest(cafeId2, "변경된카공이름", start.plusHours(5),
			start.plusHours(6), 5, false, "오픈채팅방 링크");
		//when
		sut.updateStudyOncePartially(leader.getId(), studyOnce.getId(), request);
		//then
		StudyOnce result = studyOnceRepository.findById(studyOnce.getId()).get();
		assertAll(
			() -> assertThat(result.getName()).isEqualTo(studyOnce.getName()),
			() -> assertThat(result.getCafe().getId()).isEqualTo(request.getCafeId()),
			() -> assertThat(result.getStartDateTime()).isEqualTo(studyOnce.getStartDateTime()),
			() -> assertThat(result.getEndDateTime()).isEqualTo(studyOnce.getEndDateTime()),
			() -> assertThat(result.getMaxMemberCount()).isEqualTo(request.getMaxMemberCount()),
			() -> assertThat(result.isAbleToTalk()).isEqualTo(true),
			() -> assertThat(result.getOpenChatUrl()).isEqualTo(studyOnce.getOpenChatUrl())
		);
	}

	@Test
	@DisplayName("카공장이 아니라면 카공을 수정할 수 없다.")
	void non_leader_can_not_update_study_details_partially() {
		//given
		Cafe cafe1 = cafeSaveHelper.saveCafe();
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnce(cafe1, leader);
		StudyOnceUpdateRequest request = new StudyOnceUpdateRequest(cafe1.getId(), null, null,
			null, 5, true, null);
		//then
		assertThatThrownBy(
			() -> sut.updateStudyOncePartially(member.getId(), studyOnce.getId(), request))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LEADER_PERMISSION_DENIED.getErrorMessage());
	}
}
