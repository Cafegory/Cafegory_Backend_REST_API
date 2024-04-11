package com.example.demo.domain.cafe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BusinessHourTest {
	static Stream<Arguments> existsMatchingDayOfWeekParameters() {
		return Stream.of(
			Arguments.of("MONDAY", LocalDate.of(2024, 4, 8)),
			Arguments.of("TUESDAY", LocalDate.of(2024, 4, 9)),
			Arguments.of("WEDNESDAY", LocalDate.of(2024, 4, 10)),
			Arguments.of("THURSDAY", LocalDate.of(2024, 4, 11)),
			Arguments.of("FRIDAY", LocalDate.of(2024, 4, 12)),
			Arguments.of("SATURDAY", LocalDate.of(2024, 4, 13)),
			Arguments.of("SUNDAY", LocalDate.of(2024, 4, 14))
		);
	}

	@ParameterizedTest
	@DisplayName("해당 요일의 영업 시간을 기술한 객체인지 확인")
	@MethodSource("existsMatchingDayOfWeekParameters")
	void existsMatchingDayOfWeek(String targetDay, LocalDate targetDate) {
		BusinessHour sut = BusinessHour.builder()
			.day(targetDay)
			.build();
		LocalDateTime targetDateTime = LocalDateTime.of(targetDate, LocalTime.now());

		boolean result = sut.existsMatchingDayOfWeek(targetDateTime);

		Assertions.assertThat(result)
			.isTrue();
	}
}
