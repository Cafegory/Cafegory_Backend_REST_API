package com.example.demo.domain.cafe;

import static com.example.demo.factory.TestBusinessHourFactory.*;
import static com.example.demo.util.TruncatedTimeUtil.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class BusinessHourOpenCheckerTest {

	@ParameterizedTest
	@MethodSource("provideLocalDateTime")
	@DisplayName("영업 중 여부를 판단한다.")
	void determines_if_it_is_during_business_hours(LocalDateTime now) {
		//given
		List<BusinessHour> businessHours = List.of(
			createBusinessHourWithDayAndTime("MONDAY", LocalTime.of(9, 0),
				LocalTime.of(21, 0))
		);
		BusinessHourOpenChecker sut = new BusinessHourOpenChecker();
		//when
		boolean isOpen = sut.checkWithBusinessHours(businessHours, now);
		//then
		assertThat(isOpen).isTrue();
	}

	private static Stream<Arguments> provideLocalDateTime() {
		return Stream.of(
			Arguments.of(LocalDateTime.of(2024, 1, 29, 9, 0, 0)),
			Arguments.of(LocalDateTime.of(2024, 1, 29, 12, 0, 0)),
			Arguments.of(LocalDateTime.of(2024, 1, 29, 20, 59, 59, 999_999_999))
		);
	}

	@ParameterizedTest
	@MethodSource("provideLocalDateTime5")
	@DisplayName("영업 종료 여부를 판단한다.")
	void determines_if_it_is_after_business_hours(LocalDateTime now) {
		List<BusinessHour> businessHours = List.of(
			createBusinessHourWithDayAndTime("MONDAY", LocalTime.of(9, 0),
				LocalTime.of(21, 0))
		);
		BusinessHourOpenChecker sut = new BusinessHourOpenChecker();
		//when
		boolean isOpen = sut.checkWithBusinessHours(businessHours, now);
		//then
		assertThat(isOpen).isFalse();
	}

	private static Stream<Arguments> provideLocalDateTime5() {
		return Stream.of(
			Arguments.of(LocalDateTime.of(2024, 1, 29, 8, 59, 59, 999_999_999)),
			Arguments.of(LocalDateTime.of(2024, 1, 29, 21, 0, 0))
		);
	}

	@ParameterizedTest
	@MethodSource("provideLocalDateTime2")
	@DisplayName("24시간 영업한다.")
	void open_24Hours(LocalDateTime now) {
		List<BusinessHour> businessHours = List.of(
			// createBusinessHourWithDayAndTime("MONDAY", LocalTime.of(0, 0),
			// 	LocalTime.MAX),
			// createBusinessHourWithDayAndTime("TUESDAY", LocalTime.of(0, 0),
			// 	LocalTime.MAX)
			createBusinessHourWithDayAndTime("MONDAY", LocalTime.of(0, 0),
				MAX_LOCAL_TIME),
			createBusinessHourWithDayAndTime("TUESDAY", LocalTime.of(0, 0),
				MAX_LOCAL_TIME)
		);
		BusinessHourOpenChecker sut = new BusinessHourOpenChecker();
		//when
		boolean isOpen = sut.checkWithBusinessHours(businessHours, now);
		//then
		assertThat(isOpen).isTrue();
	}

	private static Stream<Arguments> provideLocalDateTime2() {
		return Stream.of(
			Arguments.of(LocalDateTime.of(2024, 1, 29, 23, 59, 59, 999_999_999)),
			Arguments.of(LocalDateTime.of(2024, 1, 30, 0, 0))
		);
	}

	@ParameterizedTest
	@MethodSource("provideLocalDateTime3")
	@DisplayName("다음날 새벽까지 영업한다.")
	void open_until_early_morning_the_next_day(LocalDateTime now, boolean expected) {
		List<BusinessHour> businessHours = List.of(
			createBusinessHourWithDayAndTime("MONDAY", LocalTime.of(9, 0),
				LocalTime.of(2, 0)),
			createBusinessHourWithDayAndTime("TUESDAY", LocalTime.of(9, 0),
				LocalTime.of(2, 0))
		);
		BusinessHourOpenChecker sut = new BusinessHourOpenChecker();
		//when
		boolean isOpen = sut.checkWithBusinessHours(businessHours, now);
		//then
		assertThat(isOpen).isEqualTo(expected);
	}

	private static Stream<Arguments> provideLocalDateTime3() {
		return Stream.of(
			// LocalDateTime now, boolean expected
			Arguments.of(LocalDateTime.of(2024, 1, 29, 23, 59, 59, 999_999_999), true),
			Arguments.of(LocalDateTime.of(2024, 1, 30, 0, 0), true),
			Arguments.of(LocalDateTime.of(2024, 1, 30, 1, 59, 59, 999_999_999), true),
			Arguments.of(LocalDateTime.of(2024, 1, 30, 2, 0, 0), false)
		);
	}

	@ParameterizedTest
	@MethodSource("provideLocalDateTime4")
	@DisplayName("평일은 일찍마감, 금토는 24시간 오픈, 일요일은 일찍 마감")
	void business_hours_are_different_each_day(LocalDateTime now,
		boolean expected) {
		List<BusinessHour> businessHours = List.of(
			createBusinessHourWithDayAndTime("MONDAY",
				LocalTime.of(9, 0), LocalTime.of(22, 0)),
			createBusinessHourWithDayAndTime("TUESDAY",
				LocalTime.of(9, 0), MAX_LOCAL_TIME),
			createBusinessHourWithDayAndTime("FRIDAY",
				LocalTime.of(9, 0), MAX_LOCAL_TIME),
			createBusinessHourWithDayAndTime("SATURDAY",
				LocalTime.of(0, 0), MAX_LOCAL_TIME),
			createBusinessHourWithDayAndTime("SUNDAY",
				LocalTime.of(0, 0), LocalTime.of(22, 0))
		);
		BusinessHourOpenChecker sut = new BusinessHourOpenChecker();
		//when
		boolean isOpen = sut.checkWithBusinessHours(businessHours, now);
		//then
		assertThat(isOpen).isEqualTo(expected);

		/*
		365일 24시간 운영할때와는 다르게, 요일마다 운영시간이 다를때는 LocalTime.Max의 시간에서 영업중이도록 구현하지 않았다.
		nanoOfSecond가 999_999_998일경우에는 영업중, 999_999_999일경우에는 영업종료이다. 찰나의 순간이기떄문에 구현안함.
		 */
	}

	private static Stream<Arguments> provideLocalDateTime4() {
		return Stream.of(
			/*
			 29일 : 월
			 30일 : 화
			 2일 : 금
			 3일 : 토
			 4일 : 일
			*/
			Arguments.of(LocalDateTime.of(2024, 1, 29, 21, 59, 59, 999_999_000), true),
			Arguments.of(LocalDateTime.of(2024, 1, 29, 22, 0), false),
			Arguments.of(LocalDateTime.of(2024, 1, 30, 23, 59, 58), true),
			Arguments.of(LocalDateTime.of(2024, 1, 30, 23, 59, 59, 999_999_000), false),
			Arguments.of(LocalDateTime.of(2024, 2, 2, 23, 59, 58), true),
			Arguments.of(LocalDateTime.of(2024, 2, 2, 23, 59, 59, 999_999_000), false),
			Arguments.of(LocalDateTime.of(2024, 2, 3, 0, 0), true),
			Arguments.of(LocalDateTime.of(2024, 2, 4, 0, 0), true),
			Arguments.of(LocalDateTime.of(2024, 2, 4, 21, 59, 59, 999_999_000), true),
			Arguments.of(LocalDateTime.of(2024, 2, 4, 22, 0, 0), false)
		);
	}

	@ParameterizedTest
	@MethodSource("provideChosenTimeAndExpected")
	@DisplayName("영업시간 시간 사이에 선택된 시간이 포함되는지 확인한다.")
	void check_if_chosen_time_is_within_business_hours(LocalTime chosenStartTime, LocalTime chosenEndTime,
		boolean expected) {
		//given
		LocalTime businessStartTime = LocalTime.of(9, 0);
		LocalTime businessEndTime = LocalTime.of(21, 0);
		BusinessHourOpenChecker sut = new BusinessHourOpenChecker();
		//when
		boolean isBetween = sut.checkBetweenBusinessHours(businessStartTime, businessEndTime,
			chosenStartTime, chosenEndTime);
		//then
		assertThat(isBetween).isEqualTo(expected);
	}

	private static Stream<Arguments> provideChosenTimeAndExpected() {
		return Stream.of(
			Arguments.of(
				LocalTime.of(9, 0),
				LocalTime.of(21, 0),
				true
			),
			Arguments.of(
				LocalTime.of(9, 0, 0, 100_000_000),
				LocalTime.of(21, 0),
				true
			),
			Arguments.of(
				LocalTime.of(9, 0),
				LocalTime.of(20, 59, 59, 999_999_999),
				true
			),
			Arguments.of(
				LocalTime.of(9, 0, 0, 100_000_000),
				LocalTime.of(20, 59, 59, 999_999_999),
				true
			),
			Arguments.of(
				LocalTime.of(8, 59, 59, 999_999_999),
				LocalTime.of(21, 0),
				false
			),
			Arguments.of(
				LocalTime.of(9, 0, 0),
				LocalTime.of(21, 0, 1),
				false
			),
			Arguments.of(
				LocalTime.of(8, 59, 59, 999_999_999),
				LocalTime.of(21, 0, 0, 100_000_000),
				false
			)
		);
	}

	@Test
	@DisplayName("24시간 영업 시 선택된 시간이 포함되는지 확인한다.")
	void check_if_chosen_time_is_within_24hour_business_hours() {
		//given
		LocalTime businessStartTime = LocalTime.of(0, 0);
		LocalTime businessEndTime = MAX_LOCAL_TIME;
		LocalTime chosenStartTime = LocalTime.of(0, 0);
		LocalTime chosenEndTime = MAX_LOCAL_TIME;
		BusinessHourOpenChecker sut = new BusinessHourOpenChecker();
		//when
		boolean isBetween = sut.checkBetweenBusinessHours(businessStartTime, businessEndTime,
			chosenStartTime, chosenEndTime);
		//then
		assertThat(isBetween).isTrue();

	}

	@ParameterizedTest
	@MethodSource("provideChosenTimeAndExpected4")
	@DisplayName("영업시간이 다음 날 새벽까지 이어질 경우, 선택된 시간이 포함되는지 확인한다.")
	void check_if_chosen_time_is_within_overnight_business_hours(LocalTime chosenStartTime, LocalTime chosenEndTime,
		boolean expected) {
		//given
		LocalTime businessStartTime = LocalTime.of(7, 0);
		LocalTime businessEndTime = LocalTime.of(2, 0);
		BusinessHourOpenChecker sut = new BusinessHourOpenChecker();
		//when
		boolean isBetween = sut.checkBetweenBusinessHours(businessStartTime, businessEndTime,
			chosenStartTime, chosenEndTime);
		//then
		assertThat(isBetween).isEqualTo(expected);
	}

	private static Stream<Arguments> provideChosenTimeAndExpected4() {
		return Stream.of(
			Arguments.of(
				LocalTime.of(6, 59, 59, 999_999_999),
				LocalTime.of(8, 0),
				false
			),
			Arguments.of(
				LocalTime.of(7, 0),
				LocalTime.of(8, 0),
				true
			),
			Arguments.of(
				LocalTime.of(23, 0),
				MAX_LOCAL_TIME,
				true
			),
			Arguments.of(
				LocalTime.of(23, 0),
				LocalTime.of(0, 0),
				true
			),
			Arguments.of(
				LocalTime.of(23, 0),
				LocalTime.of(2, 0),
				true
			),
			Arguments.of(
				LocalTime.of(23, 0),
				LocalTime.of(2, 0, 1),
				false
			),
			Arguments.of(
				LocalTime.of(0, 0),
				LocalTime.of(1, 0),
				true
			),
			Arguments.of(
				LocalTime.of(0, 0),
				LocalTime.of(2, 0),
				true
			),
			Arguments.of(
				LocalTime.of(0, 0),
				LocalTime.of(2, 0, 1),
				false
			),
			Arguments.of(
				LocalTime.of(7, 0),
				LocalTime.of(2, 0),
				true
			),
			Arguments.of(
				LocalTime.of(6, 59, 59, 999_999_999),
				LocalTime.of(2, 0),
				false
			),
			Arguments.of(
				LocalTime.of(7, 0),
				LocalTime.of(2, 0, 1),
				false
			)
		);
	}

}
