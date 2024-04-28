package com.example.demo.domain.study;

import static com.example.demo.exception.ExceptionType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.demo.domain.member.Member;
import com.example.demo.exception.CafegoryException;

class StudyOnceTest {
	static final LocalDateTime NOW = LocalDateTime.now();
	static final long LEADER_ID = 1L;
	static final long MEMBER_ID = 2L;

	static Stream<Arguments> canJoinParameter() {
		LocalDateTime start = NOW.plusHours(4);
		return Stream.of(
			Arguments.of(start.minusHours(1), start, true),
			Arguments.of(start.minusHours(1).plusSeconds(1), start, false)
		);
	}

	@Test
	@DisplayName("카공 시작 시간이 잘못된 경우 카공을 생성할 수 없는지 확인")
	void createStudyOnceFailCauseInvalidStartTime() {
		Assertions.assertThatThrownBy(
				() -> {
					LocalDateTime startDateTime = LocalDateTime.now().plusHours(3).minusSeconds(1);
					StudyOnce.builder()
						.startDateTime(startDateTime)
						.endDateTime(startDateTime.plusHours(4))
						.build();
				})
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_WRONG_START_TIME.getErrorMessage());
	}

	@Test
	@DisplayName("카공 진행 시간이 너무 짧은 경우 카공을 생성할 수 없는지 확인")
	void createStudyOnceFailCauseShortStudyTime() {
		Assertions.assertThatThrownBy(
				() -> {
					LocalDateTime startDateTime = LocalDateTime.now().plusHours(4);
					StudyOnce.builder()
						.startDateTime(startDateTime)
						.endDateTime(startDateTime.plusMinutes(59L))
						.build();
				})
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_SHORT_DURATION.getErrorMessage());
	}

	@Test
	@DisplayName("카공 진행 시간이 너무 긴 경우 카공을 생성할 수 없는지 확인")
	void createStudyOnceFailCauseLongStudyTime() {
		Assertions.assertThatThrownBy(
				() -> {
					LocalDateTime startDateTime = LocalDateTime.now().plusHours(4);
					StudyOnce.builder()
						.startDateTime(startDateTime)
						.endDateTime(startDateTime.plusMinutes(60 * 5 + 1))
						.build();
				})
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LONG_DURATION.getErrorMessage());
	}

	@ParameterizedTest
	@MethodSource("canJoinParameter")
	@DisplayName("참여 가능 여부 결정 테스트")
	void canJoin(LocalDateTime base, LocalDateTime start, boolean expected) {
		StudyOnce studyOnce = StudyOnce.builder()
			.name("스터디 이름")
			.startDateTime(start)
			.endDateTime(start.plusHours(4))
			.leader(Member.builder().build())
			.openChatUrl("오픈채팅방 링크")
			.build();

		boolean canJoin = studyOnce.canJoin(base);

		Assertions.assertThat(canJoin)
			.isEqualTo(expected);
	}

	@Test
	@DisplayName("정상적으로 생성되었을 때 의존성 제대로 설정되었는지 확인하는 테스트")
	void create() {
		LocalDateTime start = NOW.plusHours(3).plusMinutes(1);
		Member leader = Member.builder().id(LEADER_ID).build();

		StudyOnce studyOnce = StudyOnce.builder()
			.name("스터디 이름")
			.startDateTime(start)
			.endDateTime(start.plusHours(4))
			.leader(leader)
			.openChatUrl("오픈채팅방 링크")
			.build();

		StudyOnce study = studyOnce.getStudyMembers().get(0).getStudy();
		assertAll(
			() -> Assertions.assertThat(study).isEqualTo(studyOnce),
			() -> Assertions.assertThat(studyOnce.getLeader()).isEqualTo(leader)
		);
	}

	@Test
	@DisplayName("카공 참여 테스트")
	void tryJoin() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = makeMemberWithStudyOnce(NOW.plusHours(9), NOW.plusHours(13));
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");

		studyOnce.tryJoin(member, NOW.plusHours(3).minusSeconds(1));
		List<Member> collect = studyOnce.getStudyMembers()
			.stream()
			.map(StudyMember::getMember)
			.collect(Collectors.toList());

		Assertions.assertThat(collect)
			.contains(member);
	}

	private static Member makeMemberWithStudyOnce(LocalDateTime start, LocalDateTime end) {
		Member member = Member.builder().id(MEMBER_ID).build();
		StudyMember studyMember = new StudyMember(member, makeStudy(member, "스터디 이름", start, end, "오픈채팅방 링크"));
		member.setStudyMembers(new ArrayList<>(List.of(studyMember)));
		return member;
	}

	private static StudyOnce makeStudy(Member leader, String studyName, LocalDateTime start, LocalDateTime end,
		String openChatUrl) {
		return StudyOnce.builder()
			.name(studyName)
			.startDateTime(start)
			.endDateTime(end)
			.leader(leader)
			.name(studyName)
			.openChatUrl(openChatUrl)
			.maxMemberCount(5)
			.build();
	}

	@Test
	@DisplayName("이미 카공 참여 인원이 확정되어 카공 참여 실패하는 테스트")
	void tryJoinFailByTimeOver() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = Member.builder().id(MEMBER_ID).build();

		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");

		Assertions.assertThatThrownBy(
				() -> studyOnce.tryJoin(member, NOW.plusHours(3).plusSeconds(1)))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_TOO_LATE_JOIN.getErrorMessage());
	}

	@Test
	@DisplayName("이미 참여중인 카공이 있어서 실패하는 테스트")
	void tryJoinFailByConflict() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = makeMemberWithStudyOnce(NOW.plusHours(9), NOW.plusHours(13));

		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(5), NOW.plusHours(9), "오픈채팅방 링크");

		Assertions.assertThatThrownBy(() -> studyOnce.tryJoin(member, NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CONFLICT_TIME.getErrorMessage());
	}

	@Test
	@DisplayName("이미 참여중인 카공이라 실패하는 테스트")
	void tryJoinFailByDuplicate() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = makeMemberWithStudyOnce(NOW.plusHours(9), NOW.plusHours(13));
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");

		studyOnce.tryJoin(member, NOW);

		Assertions.assertThatThrownBy(() -> studyOnce.tryJoin(member, NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_DUPLICATE.getErrorMessage());
	}

	@Test
	@DisplayName("카공 신청시 최대 인원이 넘어서 실패하는 테스트")
	void tryJoinFailByFullMember() {
		Member leader = Member.builder().id(LEADER_ID).build();

		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(7), "오픈채팅링크");
		for (int index = 0; index < 4; index++) {
			Member member = Member.builder().id(MEMBER_ID + index).build();
			studyOnce.tryJoin(member, NOW.plusSeconds(index));
		}
		Member failMember = Member.builder().id(MEMBER_ID + 4).build();
		Assertions.assertThatThrownBy(() -> studyOnce.tryJoin(failMember, NOW.plusSeconds(4)))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_FULL.getErrorMessage());
	}

	@Test
	@DisplayName("카공 참여 취소 테스트")
	void tryQuit() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = Member.builder().id(MEMBER_ID).build();

		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");
		studyOnce.tryJoin(member, NOW);

		LocalDateTime validRequestTime = NOW.plusHours(3).minusSeconds(1);
		assertDoesNotThrow(() -> studyOnce.tryQuit(member, validRequestTime));
	}

	@Test
	@DisplayName("이미 카공 참여 인원이 확정되어 카공 참여 취소가 실패하는 테스트")
	void tryQuitFailByTimeOver() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = Member.builder().id(MEMBER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");
		studyOnce.tryJoin(member, NOW);
		Assertions.assertThatThrownBy(
				() -> studyOnce.tryQuit(member, NOW.plusHours(3).plusSeconds(1)))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_TOO_LATE_QUIT.getErrorMessage());
	}

	@Test
	@DisplayName("참여중인 카공이 아니라서 카공 참여 취소가 실패하는 테스트")
	void tryQuitFailByNotJoin() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = Member.builder().id(MEMBER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");
		Assertions.assertThatThrownBy(
				() -> studyOnce.tryQuit(member, NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_TRY_QUIT_NOT_JOIN.getErrorMessage());
	}

	@Test
	@DisplayName("스터디 이름 변경, null 검증")
	void validate_null_by_changeName() {
		Member leader = Member.builder().id(LEADER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");
		assertThatThrownBy(() -> studyOnce.changeName(null))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_NAME_EMPTY_OR_WHITESPACE.getErrorMessage());
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@DisplayName("스터디 이름 변경, 빈값, 공백문자 검증")
	void validate_empty_or_whitespace_by_changeName(String value) {
		Member leader = Member.builder().id(LEADER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");
		assertThatThrownBy(() -> studyOnce.changeName(value))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_NAME_EMPTY_OR_WHITESPACE.getErrorMessage());
	}

	@Test
	@DisplayName("스터디 오픈채팅방 url 변경, null 검증")
	void validate_null_by_changeOpenChatUrl() {
		Member leader = Member.builder().id(LEADER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");
		assertThatThrownBy(() -> studyOnce.changeOpenChatUrl(null))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_OPEN_CHAT_URL_EMPTY_OR_WHITESPACE.getErrorMessage());
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@DisplayName("스터디 오픈채팅방 url 변경, 빈값, 공백문자 검증")
	void validate_empty_or_whitespace_by_changeOpenChatUrl(String value) {
		Member leader = Member.builder().id(LEADER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");
		assertThatThrownBy(() -> studyOnce.changeOpenChatUrl(value))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_OPEN_CHAT_URL_EMPTY_OR_WHITESPACE.getErrorMessage());
	}

	@Test
	@DisplayName("최대 참여인원 5명이다. 정상동작")
	void changeMaxMemberCount() {
		Member leader = Member.builder().id(LEADER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");
		assertDoesNotThrow(() -> studyOnce.changeMaxMemberCount(5));
	}

	@Test
	@DisplayName("최대 참여인원은 5명이다. 6명이면 예외가 터진다.")
	void validate_maxMemberCount_by_changeMaxMemberCount() {
		Member leader = Member.builder().id(LEADER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");
		assertThatThrownBy(() -> studyOnce.changeMaxMemberCount(6))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LIMIT_MEMBER_CAPACITY.getErrorMessage());
	}

	@Test
	@DisplayName("최대 참여 인원보다 현재 참석예정인 인원이 크다면 예외가 터진다.")
	void validate_maxOrNowMemberCount_by_changeMaxMemberCount() {
		Member leader = Member.builder().id(LEADER_ID).build();
		StudyOnce studyOnce = StudyOnce.builder()
			.name("스터디 이름")
			.startDateTime(NOW.plusHours(4))
			.endDateTime(NOW.plusHours(8))
			.maxMemberCount(4)
			.nowMemberCount(1)
			.leader(leader)
			.openChatUrl("오픈채팅방 링크")
			.build();

		assertThatThrownBy(() -> studyOnce.changeMaxMemberCount(0))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CANNOT_REDUCE_BELOW_CURRENT.getErrorMessage());
	}

	@Test
	@DisplayName("카공에 참여인원이 카공장만 있으면 true 반환")
	void doesOnlyLeaderExist_then_true() {
		Member leader = Member.builder().id(LEADER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");

		boolean doesOnlyLeaderExist = studyOnce.doesOnlyLeaderExist();
		assertThat(doesOnlyLeaderExist).isTrue();
	}

	@Test
	@DisplayName("카공에 참여인원이 여러명이라면 false 반환")
	void doesOnlyLeaderExist_then_false() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = makeMemberWithStudyOnce(NOW.plusHours(9), NOW.plusHours(13));
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");

		studyOnce.tryJoin(member, NOW.plusHours(3).minusSeconds(1));

		boolean doesOnlyLeaderExist = studyOnce.doesOnlyLeaderExist();
		assertThat(doesOnlyLeaderExist).isFalse();
	}

	@Test
	@DisplayName("멤버가 스터디에 참여했으면 true 반환")
	void isAttendance() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = makeMemberWithStudyOnce(NOW.plusHours(9), NOW.plusHours(13));
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");
		studyOnce.tryJoin(member, NOW.plusHours(3).minusSeconds(1));

		boolean isAttendance = studyOnce.isAttendance(member);
		assertThat(isAttendance).isTrue();
	}

	@Test
	@DisplayName("멤버가 스터디에 참여하지 않았으면 false 반환")
	void isNotAttendance() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = makeMemberWithStudyOnce(NOW.plusHours(9), NOW.plusHours(13));
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(8), "오픈채팅방 링크");

		boolean isAttendance = studyOnce.isAttendance(member);
		assertThat(isAttendance).isFalse();
	}

	@Test
	@DisplayName("카공 생성시 카공장이 인원에 잘 반영되는지 확인")
	void validateNowMemberCountWhenCreate() {
		Member leader = Member.builder().id(LEADER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(7), "오픈채팅링크");

		int nowMemberCount = studyOnce.getNowMemberCount();

		Assertions.assertThat(nowMemberCount)
			.isEqualTo(1);
	}

	@Test
	@DisplayName("카공 신청시 인원에 잘 반영되는지 확인")
	void validateNowMemberCountWhenJoin() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = Member.builder().id(MEMBER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, "스터디 이름", NOW.plusHours(4), NOW.plusHours(7), "오픈채팅링크");

		studyOnce.tryJoin(member, NOW.plusHours(3));
		int nowMemberCount = studyOnce.getNowMemberCount();

		Assertions.assertThat(nowMemberCount)
			.isEqualTo(2);
	}
}
