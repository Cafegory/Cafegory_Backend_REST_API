package com.example.demo.domain.study;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.demo.builder.TestMemberBuilder;

import lombok.RequiredArgsConstructor;

class StudyMemberTest {
	private static final LocalDateTime BASE_TIME = LocalDateTime.now();

	@RequiredArgsConstructor
	private static class TimeInterval {
		private final LocalDateTime start;
		private final LocalDateTime end;

		@Override
		public String toString() {
			return start.toString() + " ~ " + end.toString();
		}
	}

	static Stream<Arguments> isConflictParameters() {
		return Stream.of(
			Arguments.of(new TimeInterval(BASE_TIME, BASE_TIME.plusHours(4).minusSeconds(1)), false),
			Arguments.of(new TimeInterval(BASE_TIME, BASE_TIME.plusHours(4)), true),
			Arguments.of(new TimeInterval(BASE_TIME.plusHours(5), BASE_TIME.plusHours(6)), true),
			Arguments.of(new TimeInterval(BASE_TIME.plusHours(5), BASE_TIME.plusHours(7)), true),
			Arguments.of(new TimeInterval(BASE_TIME.plusHours(5), BASE_TIME.plusHours(8)), true),
			Arguments.of(new TimeInterval(BASE_TIME.plusHours(7), BASE_TIME.plusHours(9)), true),
			Arguments.of(new TimeInterval(BASE_TIME.plusHours(7).plusSeconds(1), BASE_TIME.plusHours(9)), false)
		);
	}

	@ParameterizedTest
	@MethodSource("isConflictParameters")
	@DisplayName("카공 시작 시간이 주어진 시간의 끝과 겹치는 경우")
	void isConflict(TimeInterval timeInterval, boolean expected) {
		LocalDateTime start = BASE_TIME.plusHours(4);
		LocalDateTime end = BASE_TIME.plusHours(7);
		StudyOnce studyOnce = makeStudyOnce(start, end);
		StudyMember studyMember = makeStudyMember(studyOnce);

		boolean actual = studyMember.isConflictWith(timeInterval.start, timeInterval.end);

		Assertions.assertThat(actual)
			.isEqualTo(expected);
	}

	private StudyOnce makeStudyOnce(LocalDateTime start, LocalDateTime end) {
		return StudyOnce.builder()
			.startDateTime(start)
			.endDateTime(end)
			.leader(new TestMemberBuilder().build())
			.build();
	}

	private StudyMember makeStudyMember(StudyOnce studyOnce) {
		return StudyMember.builder()
			.member(new TestMemberBuilder().build())
			.study(studyOnce)
			.build();
	}
}
