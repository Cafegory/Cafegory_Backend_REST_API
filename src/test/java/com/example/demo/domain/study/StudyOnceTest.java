package com.example.demo.domain.study;

import static com.example.demo.exception.ExceptionType.*;
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
			.startDateTime(start)
			.endDateTime(start.plusHours(4))
			.leader(Member.builder().build())
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
			.startDateTime(start)
			.endDateTime(start.plusHours(4))
			.leader(leader)
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
		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(4), NOW.plusHours(8));

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
		StudyMember studyMember = new StudyMember(member, makeStudy(member, start, end));
		member.setStudyMembers(new ArrayList<>(List.of(studyMember)));
		return member;
	}

	private static StudyOnce makeStudy(Member leader, LocalDateTime start, LocalDateTime end) {
		return StudyOnce.builder()
			.startDateTime(start)
			.endDateTime(end)
			.leader(leader)
			.build();
	}

	@Test
	@DisplayName("이미 카공 참여 인원이 확정되어 카공 참여 실패하는 테스트")
	void tryJoinFailByTimeOver() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = Member.builder().id(MEMBER_ID).build();

		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(4), NOW.plusHours(8));

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

		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(5), NOW.plusHours(9));

		Assertions.assertThatThrownBy(() -> studyOnce.tryJoin(member, NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CONFLICT_TIME.getErrorMessage());
	}

	@Test
	@DisplayName("이미 참여중인 카공이라 실패하는 테스트")
	void tryJoinFailByDuplicate() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = makeMemberWithStudyOnce(NOW.plusHours(9), NOW.plusHours(13));
		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(4), NOW.plusHours(8));

		studyOnce.tryJoin(member, NOW);

		Assertions.assertThatThrownBy(() -> studyOnce.tryJoin(member, NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_DUPLICATE.getErrorMessage());
	}

	@Test
	@DisplayName("카공 참여 취소 테스트")
	void tryQuit() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = Member.builder().id(MEMBER_ID).build();

		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(4), NOW.plusHours(8));
		studyOnce.tryJoin(member, NOW);

		LocalDateTime validRequestTime = NOW.plusHours(3).minusSeconds(1);
		assertDoesNotThrow(() -> studyOnce.tryQuit(member, validRequestTime));
	}

	@Test
	@DisplayName("이미 카공 참여 인원이 확정되어 카공 참여 취소가 실패하는 테스트")
	void tryQuitFailByTimeOver() {
		Member leader = Member.builder().id(LEADER_ID).build();
		Member member = Member.builder().id(MEMBER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(4), NOW.plusHours(8));
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
		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(4), NOW.plusHours(8));
		Assertions.assertThatThrownBy(
				() -> studyOnce.tryQuit(member, NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_TRY_QUIT_NOT_JOIN.getErrorMessage());
	}
}
