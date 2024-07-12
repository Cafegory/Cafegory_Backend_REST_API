package com.example.demo.domain.study;

import static com.example.demo.exception.ExceptionType.*;
import static com.example.demo.factory.TestCafeFactory.*;
import static com.example.demo.factory.TestMemberFactory.*;
import static com.example.demo.factory.TestStudyOnceFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.exception.CafegoryException;

class StudyOnceTest {
	private static final LocalDateTime NOW = LocalDateTime.now();
	private static final long LEADER_ID = 1L;
	private static final long MEMBER_ID = 2L;

	@ParameterizedTest
	@MethodSource("provideTimeAndExpected")
	@DisplayName("카공 시작시간 1시간 전까지 카공 참여신청이 가능하다.")
	void allows_joining_until_1hour_before_start(LocalDateTime joiningTime, LocalDateTime studyStartTime,
		boolean expected) {
		//given
		Cafe cafe = createCafe();
		Member leader = createMember();
		StudyOnce sut = createStudyOnceWithTime(cafe, leader, studyStartTime,
			studyStartTime.plusHours(4));
		//when
		boolean canJoin = sut.canJoin(joiningTime);
		//then
		assertThat(canJoin).isEqualTo(expected);
	}

	private static Stream<Arguments> provideTimeAndExpected() {
		LocalDateTime start = NOW.plusHours(4);
		return Stream.of(
			Arguments.of(start.minusHours(1), start, true),
			Arguments.of(start.minusHours(1).plusSeconds(1), start, false)
		);
	}

	@Test
	@DisplayName("카공 시작시간 1시간 전까지 카공 참여신청이 가능하다.")
	void allows_joining_until_1hour_before_start() {
		//given
		Member leader = createMember();
		Member member = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnceWithTime(cafe, leader, NOW.plusHours(4), NOW.plusHours(6));
		//when
		sut.tryJoin(member, NOW.plusHours(3));
		//then
		assertThat(sut.getStudyMembers().size()).isEqualTo(2);
	}

	@Test
	@DisplayName("카공 시작시간이 1시간도 안남으면 카공 참여신청이 불가능하다.")
	void join_fails_when_less_than_1hour_before_start() {
		//given
		Member leader = createMember();
		Member member = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnceWithTime(cafe, leader, NOW.plusHours(4), NOW.plusHours(6));
		//when
		assertThatThrownBy(
			() -> sut.tryJoin(member, NOW.plusHours(3).plusNanos(100_000_000)))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_TOO_LATE_JOIN.getErrorMessage());
	}

	/*@Test
	@DisplayName("이미 참여중인 카공과 시간이 겹칠 경우 카공 참여신청이 불가능하다.")
	void join_fails_when_time_conflicts() {
		*//*
		이 테스트가 성공하도록 리팩토링해야함
		Member 에서 StudyMember를 분리해야함
		 *//*
		//given
		Member leader = createMember();
		Member member = createMember();
		Cafe cafe = createCafe();
		createStudyOnceWithTime(cafe, member, NOW.plusHours(4), NOW.plusHours(6));
		StudyOnce sut = createStudyOnceWithTime(cafe, leader, NOW.plusHours(4), NOW.plusHours(6));
		//then
		assertThatThrownBy(() -> sut.tryJoin(member, NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CONFLICT_TIME.getErrorMessage());
	}*/

	@Test
	@DisplayName("이미 참여중인 카공에 다시 참여 신청할 수 없다.")
	void join_fails_for_already_participating_study() {
		//given
		Member leader = createMember();
		Member member = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		//when
		sut.tryJoin(member, NOW);
		//then
		assertThatThrownBy(() -> sut.tryJoin(member, NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_DUPLICATE.getErrorMessage());
	}

	@Test
	@DisplayName("최대인원 초과 시 카공 신청이 불가능하다.")
	void join_fails_when_study_is_full() {
		//given
		Member leader = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		for (int i = 0; i < 4; i++) {
			Member member = createMember();
			sut.tryJoin(member, NOW.plusSeconds(i));
		}
		//when
		Member failMember = createMember();
		//then
		assertThatThrownBy(() -> sut.tryJoin(failMember, NOW.plusSeconds(4)))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_FULL.getErrorMessage());
	}


/*	@Test
	@DisplayName("카공 참여를 취소한다.")
	void cancel_study_participation_successfully() {
		*//*
		이 테스트가 성공하도록 리팩토링해야함
		Member 에서 StudyMember를 분리해야함
		 *//*
		//given
		Member leader = createMember();
		Member member = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		sut.tryJoin(member, NOW);
		//then
		assertDoesNotThrow(() -> sut.tryQuit(member, NOW.plusSeconds(1)));
	}*/

	@Test
	@DisplayName("카공 인원이 확정되면 카공 참여취소가 불가능하다.")
	void can_not_quit_study_after_confirmation() {
		//given
		Member leader = createMember();
		Member member = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnceWithTime(cafe, leader, NOW.plusHours(4), NOW.plusHours(6));
		sut.tryJoin(member, NOW);
		//then
		assertThatThrownBy(
			() -> sut.tryQuit(member, NOW.plusHours(3).plusNanos(100_000_000)))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_TOO_LATE_QUIT.getErrorMessage());
	}

	/*@Test
	@DisplayName("참여중인 카공이 아니라면 카공 참여취소가 불가능하다.")
	void can_not_quit_if_not_participant() {
		*//*
		이 테스트가 성공하도록 리팩토링해야함
		Member 에서 StudyMember를 분리해야함
		 *//*
		//given
		Member leader = createMember();
		Member member = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		//then
		assertThatThrownBy(
			() -> sut.tryQuit(member, NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_TRY_QUIT_NOT_JOIN.getErrorMessage());
	}*/

	@Test
	@DisplayName("스터디 이름 변경, null 검증")
	void name_is_null() {
		//given
		Member leader = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		//then
		assertThatThrownBy(() -> sut.changeName(null))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_NAME_EMPTY_OR_WHITESPACE.getErrorMessage());
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@DisplayName("스터디 이름 변경, 빈값, 공백문자 검증")
	void name_is_empty_or_whitespace(String value) {
		//given
		Member leader = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		assertThatThrownBy(() -> sut.changeName(value))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_NAME_EMPTY_OR_WHITESPACE.getErrorMessage());
	}

	@Test
	@DisplayName("스터디 오픈채팅방 url 변경, null 검증")
	void open_chat_url_is_null() {
		//given
		Member leader = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		//then
		assertThatThrownBy(() -> sut.changeOpenChatUrl(null))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_OPEN_CHAT_URL_EMPTY_OR_WHITESPACE.getErrorMessage());
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@DisplayName("스터디 오픈채팅방 url 변경, 빈값, 공백문자 검증")
	void open_chat_url_is_empty_or_whitespace(String value) {
		//given
		Member leader = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		//then
		assertThatThrownBy(() -> sut.changeOpenChatUrl(value))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_OPEN_CHAT_URL_EMPTY_OR_WHITESPACE.getErrorMessage());
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 5})
	@DisplayName("최대 참여 인원수를 변경한다.")
	void change_max_member_count(int maxMemberCount) {
		//given
		Member leader = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		//then
		assertDoesNotThrow(() -> sut.changeMaxMemberCount(maxMemberCount));
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 6})
	@DisplayName("가능한 최대 참여 인원수는 1명 이상 5명 이하이다.")
	void verify_max_member_count_restrictions(int maxMemberCount) {
		//given
		Member leader = createMember();
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		//then
		assertThatThrownBy(() -> sut.changeMaxMemberCount(6))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_LIMIT_MEMBER_CAPACITY.getErrorMessage());
	}

	@Test
	@DisplayName("카공 참여자에 카공장만 존재한다.")
	void only_leader_exists_in_study() {
		//given
		Member leader = createMemberWithId(LEADER_ID);
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		//when
		boolean doesOnlyLeaderExist = sut.doesOnlyLeaderExist();
		//then
		assertThat(doesOnlyLeaderExist).isTrue();
	}

	@Test
	@DisplayName("카공 참여자에 여러명이 존재한다.")
	void several_members_exists_in_study() {
		//given
		Member leader = createMemberWithId(LEADER_ID);
		Member member = createMemberWithId(MEMBER_ID);
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		sut.tryJoin(member, NOW.plusHours(3).minusSeconds(1));
		//when
		boolean doesOnlyLeaderExist = sut.doesOnlyLeaderExist();
		//then
		assertThat(doesOnlyLeaderExist).isFalse();
	}

	@Test
	@DisplayName("회원이 카공에 참여한 상태다.")
	void member_has_joined_study() {
		//given
		Member leader = createMemberWithId(LEADER_ID);
		Member member = createMemberWithId(MEMBER_ID);
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		sut.tryJoin(member, NOW.plusHours(3).minusSeconds(1));
		//when
		boolean isAttendance = sut.isAttendance(member);
		//then
		assertThat(isAttendance).isTrue();
	}

	@Test
	@DisplayName("회원이 카공에 참여하지 않은 상태다.")
	void member_has_not_joined_study() {
		//given
		Member leader = createMemberWithId(LEADER_ID);
		Member member = createMemberWithId(MEMBER_ID);
		Cafe cafe = createCafe();
		StudyOnce sut = createStudyOnce(cafe, leader);
		//when
		boolean isAttendance = sut.isAttendance(member);
		//then
		assertThat(isAttendance).isFalse();
	}
}
