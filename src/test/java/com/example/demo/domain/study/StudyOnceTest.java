package com.example.demo.domain.study;

import static com.example.demo.exception.ExceptionType.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.demo.domain.member.MemberImpl;
import com.example.demo.exception.CafegoryException;

class StudyOnceTest {
	static final LocalDateTime NOW = LocalDateTime.now();
	static final long LEADER_ID = 1L;
	static final long MEMBER_ID = 2L;

	@ParameterizedTest
	@MethodSource("canJoinParameter")
	@DisplayName("참여 가능 여부 결정 테스트")
	void canJoin(LocalDateTime base, LocalDateTime start, boolean expected) {
		StudyOnce studyOnce = StudyOnce.builder()
			.startDateTime(start)
			.endDateTime(start.plusHours(4))
			.leader(MemberImpl.builder().build())
			.build();
		boolean canJoin = studyOnce.canJoin(base);
		Assertions.assertEquals(expected, canJoin);
	}

	@Test
	@DisplayName("정상적으로 생성되었을 때 의존성 제대로 설정되었는지 확인하는 테스트")
	void create() {
		LocalDateTime start = NOW.plusHours(3).plusMinutes(1);
		MemberImpl leader = MemberImpl.builder().id(LEADER_ID).build();
		StudyOnce studyOnce = StudyOnce.builder()
			.startDateTime(start)
			.endDateTime(start.plusHours(4))
			.leader(leader)
			.build();
		Assertions.assertEquals(studyOnce, studyOnce.getStudyMembers().get(0).getStudy());
		Assertions.assertEquals(studyOnce.getLeader(), studyOnce.getStudyMembers().get(0).getMember());
	}

	static Stream<Arguments> canJoinParameter() {
		LocalDateTime start = NOW.plusHours(4);
		return Stream.of(
			Arguments.of(start.minusHours(1), start, true),
			Arguments.of(start.minusHours(1).plusSeconds(1), start, false)
		);
	}

	@Test
	@DisplayName("카공 참여 테스트")
	void tryJoin() {
		MemberImpl leader = MemberImpl.builder().id(LEADER_ID).build();
		MemberImpl member = makeMemberWithStudyOnce(NOW.plusHours(9), NOW.plusHours(13));
		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(4), NOW.plusHours(8));
		studyOnce.tryJoin(member, NOW.plusHours(3).minusSeconds(1));
		List<MemberImpl> collect = studyOnce.getStudyMembers()
			.stream()
			.map(StudyMember::getMember)
			.collect(Collectors.toList());
		org.assertj.core.api.Assertions.assertThat(collect).contains(member);
	}

	private static StudyOnce makeStudy(MemberImpl leader, LocalDateTime start, LocalDateTime end) {
		return StudyOnce.builder()
			.startDateTime(start)
			.endDateTime(end)
			.leader(leader)
			.build();
	}

	private static MemberImpl makeMemberWithStudyOnce(LocalDateTime start, LocalDateTime end) {
		MemberImpl member = MemberImpl.builder().id(MEMBER_ID).build();
		StudyMember studyMember = new StudyMember(member, makeStudy(member, start, end));
		member.setStudyMembers(new ArrayList<>(List.of(studyMember)));
		return member;
	}

	@Test
	@DisplayName("이미 카공 참여 인원이 확정되어 카공 참여 실패하는 테스트")
	void tryJoinFailByTimeOver() {
		MemberImpl leader = MemberImpl.builder().id(LEADER_ID).build();
		MemberImpl member = MemberImpl.builder().id(MEMBER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(4), NOW.plusHours(8));
		org.assertj.core.api.Assertions.assertThatThrownBy(
				() -> studyOnce.tryJoin(member, NOW.plusHours(3).plusSeconds(1)))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_TOO_LATE_JOIN.getErrorMessage());
	}

	@Test
	@DisplayName("이미 참여중인 카공이 있어서 실패하는 테스트")
	void tryJoinFailByConflict() {
		MemberImpl leader = MemberImpl.builder().id(LEADER_ID).build();
		MemberImpl member = makeMemberWithStudyOnce(NOW.plusHours(9), NOW.plusHours(13));
		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(5), NOW.plusHours(9));
		org.assertj.core.api.Assertions.assertThatThrownBy(() -> studyOnce.tryJoin(member, NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_CONFLICT_TIME.getErrorMessage());
	}

	@Test
	@DisplayName("이미 참여중인 카공이라 실패하는 테스트")
	void tryJoinFailByDuplicate() {
		MemberImpl leader = MemberImpl.builder().id(LEADER_ID).build();
		MemberImpl member = makeMemberWithStudyOnce(NOW.plusHours(9), NOW.plusHours(13));
		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(4), NOW.plusHours(8));
		studyOnce.tryJoin(member, NOW);
		org.assertj.core.api.Assertions.assertThatThrownBy(() -> studyOnce.tryJoin(member, NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_DUPLICATE.getErrorMessage());
	}

	@Test
	@DisplayName("카공 참여 취소 테스트")
	void tryQuit() {
		MemberImpl leader = MemberImpl.builder().id(LEADER_ID).build();
		MemberImpl member = MemberImpl.builder().id(MEMBER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(4), NOW.plusHours(8));
		studyOnce.tryJoin(member, NOW);
		studyOnce.tryQuit(member, NOW.plusHours(3).minusSeconds(1));
	}

	@Test
	@DisplayName("이미 카공 참여 인원이 확정되어 카공 참여 취소가 실패하는 테스트")
	void tryQuitFailByTimeOver() {
		MemberImpl leader = MemberImpl.builder().id(LEADER_ID).build();
		MemberImpl member = MemberImpl.builder().id(MEMBER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(4), NOW.plusHours(8));
		studyOnce.tryJoin(member, NOW);
		org.assertj.core.api.Assertions.assertThatThrownBy(
				() -> studyOnce.tryQuit(member, NOW.plusHours(3).plusSeconds(1)))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_TOO_LATE_QUIT.getErrorMessage());
	}

	@Test
	@DisplayName("참여중인 카공이 아니라서 카공 참여 취소가 실패하는 테스트")
	void tryQuitFailByNotJoin() {
		MemberImpl leader = MemberImpl.builder().id(LEADER_ID).build();
		MemberImpl member = MemberImpl.builder().id(MEMBER_ID).build();
		StudyOnce studyOnce = makeStudy(leader, NOW.plusHours(4), NOW.plusHours(8));
		org.assertj.core.api.Assertions.assertThatThrownBy(
				() -> studyOnce.tryQuit(member, NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(STUDY_ONCE_TRY_QUIT_NOT_JOIN.getErrorMessage());
	}
}
