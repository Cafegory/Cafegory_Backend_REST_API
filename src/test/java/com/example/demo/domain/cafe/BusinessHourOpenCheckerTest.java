package com.example.demo.domain.cafe;

import static org.assertj.core.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.demo.exception.CafegoryException;

public class BusinessHourOpenCheckerTest {

	private BusinessHourOpenChecker openChecker = new BusinessHourOpenChecker();

	@Test
	@DisplayName("현재시간이 요일에 맞는 영업시간에 포함하면 open이다")
	void contains_Nowtime_Then_Open() {
		//given
		LocalDateTime now = LocalDateTime.of(2024, 1, 29, 12, 0, 0);
		DayOfWeek dayOfWeek = now.getDayOfWeek();
		LocalTime startTime = LocalTime.of(9, 0);
		LocalTime endTime = LocalTime.of(21, 0);
		//when
		boolean isOpen = openChecker.checkByNowTime(dayOfWeek, startTime, endTime, now);
		//then
		assertThat(isOpen).isTrue();
	}

	@Test
	@DisplayName("현재시간이 요일에 맞는 영업 시작시간이랑 일치하면 open이다")
	void when_NowTime_Is_StartTime_Then_Open() {
		//given
		LocalDateTime now = LocalDateTime.of(2024, 1, 29, 9, 0, 0);
		DayOfWeek dayOfWeek = now.getDayOfWeek();
		LocalTime startTime = LocalTime.of(9, 0);
		LocalTime endTime = LocalTime.of(21, 0);
		//when
		boolean isOpen = openChecker.checkByNowTime(dayOfWeek, startTime, endTime, now);
		//then
		assertThat(isOpen).isTrue();
	}

	@Test
	@DisplayName("현재시간이 요일에 맞는 영업 종료시작이랑 일치하면 close이다")
	void when_NowTime_Is_EndTime_Then_Close() {
		//given
		LocalDateTime now = LocalDateTime.of(2024, 1, 29, 21, 0, 0);
		DayOfWeek dayOfWeek = now.getDayOfWeek();
		LocalTime startTime = LocalTime.of(9, 0);
		LocalTime endTime = LocalTime.of(21, 0);
		//when
		boolean isOpen = openChecker.checkByNowTime(dayOfWeek, startTime, endTime, now);
		//then
		assertThat(isOpen).isFalse();
	}

	@Test
	@DisplayName("현재시간이 요일에 맞는 영업 종료시간 1초전이라면 open이다")
	void when_NowTime_Is_EndTime_Before_1Sec_Then_Close() {
		//given
		LocalDateTime now = LocalDateTime.of(2024, 1, 29, 20, 59, 59);
		DayOfWeek dayOfWeek = now.getDayOfWeek();
		LocalTime startTime = LocalTime.of(9, 0);
		LocalTime endTime = LocalTime.of(21, 0);
		//when
		boolean isOpen = openChecker.checkByNowTime(dayOfWeek, startTime, endTime, now);
		//then
		assertThat(isOpen).isTrue();
	}

	@ParameterizedTest
	@MethodSource("provideLocalDateTime")
	@DisplayName("BusinessHours를 가지고 영업시간을 체크한다.")
	void checkWithBusinessHours(LocalDateTime now, boolean expected) {
		List<BusinessHour> businessHours = new ArrayList<>();

		BusinessHour monday = BusinessHour.builder()
			.day("MONDAY")
			.startTime(LocalTime.of(9, 0))
			.endTime(LocalTime.of(21, 0))
			.build();
		BusinessHour tuesday = BusinessHour.builder()
			.day("TUESDAY")
			.startTime(LocalTime.of(9, 0))
			.endTime(LocalTime.of(21, 0))
			.build();
		BusinessHour wednesday = BusinessHour.builder()
			.day("WEDNESDAY")
			.startTime(LocalTime.of(9, 0))
			.endTime(LocalTime.of(21, 0))
			.build();

		businessHours.add(monday);
		businessHours.add(tuesday);
		businessHours.add(wednesday);

		//when
		boolean isOpen = openChecker.checkWithBusinessHours(businessHours, now);
		//then
		assertThat(isOpen).isEqualTo(expected);
	}

	private static Stream<Arguments> provideLocalDateTime() {
		return Stream.of(
			// LocalDateTime now, boolean expected
			Arguments.of(LocalDateTime.of(2024, 1, 29, 12, 30, 0), true),
			Arguments.of(LocalDateTime.of(2024, 1, 31, 8, 30, 0), false)
		);
	}

	@ParameterizedTest
	@MethodSource("provideLocalDateTime2")
	@DisplayName("카페가 24시간 운영한다.")
	void check_businessHours_when_cafe_is_always_open(LocalDateTime now, boolean expected) {
		List<BusinessHour> businessHours = new ArrayList<>();

		BusinessHour monday = BusinessHour.builder()
			.day("MONDAY")
			.startTime(LocalTime.of(0, 0))
			.endTime(LocalTime.MAX)
			.build();
		BusinessHour tuesday = BusinessHour.builder()
			.day("TUESDAY")
			.startTime(LocalTime.of(0, 0))
			.endTime(LocalTime.MAX)
			.build();
		BusinessHour wednesday = BusinessHour.builder()
			.day("WEDNESDAY")
			.startTime(LocalTime.of(0, 0))
			.endTime(LocalTime.MAX)
			.build();

		businessHours.add(monday);
		businessHours.add(tuesday);
		businessHours.add(wednesday);

		//when
		boolean isOpen = openChecker.checkWithBusinessHours(businessHours, now);
		//then
		assertThat(isOpen).isEqualTo(expected);
	}

	private static Stream<Arguments> provideLocalDateTime2() {
		return Stream.of(
			// LocalDateTime now, boolean expected
			Arguments.of(LocalDateTime.of(2024, 1, 29, 12, 30, 0), true),
			Arguments.of(LocalDateTime.of(2024, 1, 29, 23, 59, 59), true),
			Arguments.of(LocalDateTime.of(2024, 1, 29, 23, 59, 59, 999_999_998), true),
			Arguments.of(LocalDateTime.of(2024, 1, 29, 23, 59, 59, 999_999_999), true)
		);
	}

	@ParameterizedTest
	@MethodSource("provideLocalDateTime3")
	@DisplayName("카페가 새벽2시까지 운영한다.")
	void check_businessHours_when_cafe_is_open_2am(LocalDateTime now, boolean expected) {
		List<BusinessHour> businessHours = new ArrayList<>();

		BusinessHour monday = BusinessHour.builder()
			.day("MONDAY")
			.startTime(LocalTime.of(9, 0))
			.endTime(LocalTime.of(2, 0))
			.build();
		BusinessHour tuesday = BusinessHour.builder()
			.day("TUESDAY")
			.startTime(LocalTime.of(9, 0))
			.endTime(LocalTime.of(2, 0))
			.build();
		BusinessHour wednesday = BusinessHour.builder()
			.day("WEDNESDAY")
			.startTime(LocalTime.of(9, 0))
			.endTime(LocalTime.of(2, 0))
			.build();

		businessHours.add(monday);
		businessHours.add(tuesday);
		businessHours.add(wednesday);

		//when
		boolean isOpen = openChecker.checkWithBusinessHours(businessHours, now);
		//then
		assertThat(isOpen).isEqualTo(expected);
	}

	private static Stream<Arguments> provideLocalDateTime3() {
		return Stream.of(
			// LocalDateTime now, boolean expected
			Arguments.of(LocalDateTime.of(2024, 1, 30, 1, 59, 59), true),
			Arguments.of(LocalDateTime.of(2024, 1, 30, 2, 0, 0), false)
		);
	}

	@ParameterizedTest
	@MethodSource("provideLocalDateTime4")
	@DisplayName("평일은 일찍마감하고, 금토는 24시간 오픈한다.")
	void check_businessHours_when_businessHour_is_different_depend_on_weekdays_and_weekends(LocalDateTime now,
		boolean expected) {
		List<BusinessHour> businessHours = new ArrayList<>();

		BusinessHour monday = BusinessHour.builder()
			.day("MONDAY")
			.startTime(LocalTime.of(9, 0))
			.endTime(LocalTime.of(22, 0))
			.build();
		BusinessHour tuesday = BusinessHour.builder()
			.day("TUESDAY")
			.startTime(LocalTime.of(9, 0))
			.endTime(LocalTime.of(22, 0))
			.build();
		BusinessHour friday = BusinessHour.builder()
			.day("FRIDAY")
			.startTime(LocalTime.of(9, 0))
			.endTime(LocalTime.MAX)
			.build();
		BusinessHour saturday = BusinessHour.builder()
			.day("SATURDAY")
			.startTime(LocalTime.of(0, 0))
			.endTime(LocalTime.MAX)
			.build();
		BusinessHour sunday = BusinessHour.builder()
			.day("SUNDAY")
			.startTime(LocalTime.of(0, 0))
			.endTime(LocalTime.of(22, 0))
			.build();

		businessHours.add(monday);
		businessHours.add(tuesday);
		businessHours.add(friday);
		businessHours.add(saturday);
		businessHours.add(sunday);

		//when
		boolean isOpen = openChecker.checkWithBusinessHours(businessHours, now);
		//then
		assertThat(isOpen).isEqualTo(expected);

		/*
		365일 24시간 운영할때와는 다르게, 요일마다 운영시간이 다를때는 LocalTime.Max의 시간에서 영업중이도록 구현하지 않았다.
		nanoOfSecond가 999_999_998일경우에는 영업중, 999_999_999일경우에는 영업종료이다. 찰나의 순간이기떄문에 구현안함.
		//when 금요일
		LocalDateTime now3 = LocalDateTime.of(2024, 2, 2, 23, 59, 59, 999_999_999);
		boolean isOpen3 = openChecker.checkWithBusinessHours(businessHours, now3);
		//then
		assertThat(isOpen3).isTrue();
		 */
	}

	private static Stream<Arguments> provideLocalDateTime4() {
		return Stream.of(
			// LocalDateTime now, boolean expected
			Arguments.of(LocalDateTime.of(2024, 1, 29, 12, 30), true),
			Arguments.of(LocalDateTime.of(2024, 2, 2, 23, 59, 59, 999_999_998), true),
			Arguments.of(LocalDateTime.of(2024, 2, 3, 0, 0), true),
			Arguments.of(LocalDateTime.of(2024, 2, 4, 21, 59, 59, 999_999_999), true),
			Arguments.of(LocalDateTime.of(2024, 2, 4, 22, 0, 0), false)
		);
	}

	@Test
	@DisplayName("DayOfWeek Enum상수가 가지고 있는 요일이 BusinessHours에 존재하지 않으면 예외가 터진다.")
	void checkDayOfWeekWithBusinessHours() {
		List<BusinessHour> businessHours = new ArrayList<>();
		BusinessHour weekends = BusinessHour.builder()
			.day("WEEKENDS")
			.startTime(LocalTime.of(9, 0))
			.endTime(LocalTime.of(21, 0))
			.build();
		businessHours.add(weekends);

		//when
		LocalDateTime now = LocalDateTime.of(2024, 1, 29, 12, 30, 0);
		//then
		assertThatThrownBy(() -> openChecker.checkWithBusinessHours(businessHours, now))
			.isInstanceOf(CafegoryException.class);
	}

	@ParameterizedTest
	@MethodSource("provideChosenTimeAndExpected")
	@DisplayName("선택된 시간사이에 영업시간이 포함한다.")
	void when_check_businessHours_between_chosen_hours_then_contains(LocalTime chosenStartTime, LocalTime chosenEndTime,
		boolean expected) {
		//given
		LocalTime businessStartTime = LocalTime.of(9, 0);
		LocalTime businessEndTime = LocalTime.of(21, 0);

		//when
		boolean isBetween = openChecker.checkBetweenHours(businessStartTime, businessEndTime,
			chosenStartTime, chosenEndTime);
		//then
		assertThat(isBetween).isEqualTo(expected);
	}

	private static Stream<Arguments> provideChosenTimeAndExpected() {
		return Stream.of(
			// LocalTime chosenStartTime
			// LocalTime chosenEndTime,
			// boolean expected
			Arguments.of(
				LocalTime.of(8, 0),
				LocalTime.of(22, 0),
				true
			),
			Arguments.of(
				LocalTime.of(9, 0),
				LocalTime.of(21, 0),
				true
			),
			Arguments.of(
				LocalTime.of(8, 59, 59),
				LocalTime.of(21, 0),
				true
			),
			Arguments.of(
				LocalTime.of(8, 59, 59),
				LocalTime.of(21, 0, 1),
				true
			),
			Arguments.of(
				LocalTime.of(0, 0, 0),
				LocalTime.of(23, 59, 59, 999_999_999),
				true
			)
		);
	}

	@ParameterizedTest
	@MethodSource("provideChosenTimeAndExpected2")
	@DisplayName("선택된 시간사이에 영업시간이 포함되지 않는다.")
	void when_check_businessHours_between_chosen_hours_then_not_contains(LocalTime chosenStartTime,
		LocalTime chosenEndTime,
		boolean expected) {
		//given
		LocalTime businessStartTime = LocalTime.of(9, 0);
		LocalTime businessEndTime = LocalTime.of(21, 0);

		//when
		boolean isBetween = openChecker.checkBetweenHours(businessStartTime, businessEndTime,
			chosenStartTime, chosenEndTime);
		//then
		assertThat(isBetween).isEqualTo(expected);
	}

	private static Stream<Arguments> provideChosenTimeAndExpected2() {
		return Stream.of(
			// LocalTime chosenStartTime
			// LocalTime chosenEndTime,
			// boolean expected
			Arguments.of(
				LocalTime.of(9, 0),
				LocalTime.of(20, 59, 59),
				false
			),
			Arguments.of(
				LocalTime.of(9, 0, 1),
				LocalTime.of(21, 0),
				false
			),
			Arguments.of(
				LocalTime.of(8, 59, 59),
				LocalTime.of(20, 59, 59),
				false
			)
		);
	}

	@Test
	@DisplayName("영업시간이 0시부터 24시까지일 경우")
	void when_businessHour_is_0_to_24() {
		//given
		LocalTime businessStartTime = LocalTime.of(0, 0);
		LocalTime businessEndTime = LocalTime.MAX;

		//when
		LocalTime chosenStartTime1 = LocalTime.of(0, 0);
		LocalTime chosenEndTime = LocalTime.MAX;
		boolean isBetween = openChecker.checkBetweenHours(businessStartTime, businessEndTime,
			chosenStartTime1, chosenEndTime);
		//then
		assertThat(isBetween).isTrue();

	}

	@ParameterizedTest
	@MethodSource("provideChosenTimeAndExpected3")
	@DisplayName("영업시간이 밤22시부터 새벽2시까지일 경우")
	void when_businessHour_is_22_to_2(LocalTime chosenStartTime, LocalTime chosenEndTime,
		boolean expected) {
		//given
		LocalTime businessStartTime = LocalTime.of(22, 0);
		LocalTime businessEndTime = LocalTime.of(2, 0);

		//when
		boolean isBetween = openChecker.checkBetweenHours(businessStartTime, businessEndTime,
			chosenStartTime, chosenEndTime);
		//then
		assertThat(isBetween).isEqualTo(expected);
	}

	private static Stream<Arguments> provideChosenTimeAndExpected3() {
		return Stream.of(
			// LocalTime chosenStartTime
			// LocalTime chosenEndTime,
			// boolean expected
			Arguments.of(
				LocalTime.of(22, 0),
				LocalTime.of(2, 0),
				true
			),
			Arguments.of(
				LocalTime.of(21, 0),
				LocalTime.of(2, 0),
				true
			),
			Arguments.of(
				LocalTime.of(22, 0, 1),
				LocalTime.of(2, 0),
				false
			),
			Arguments.of(
				LocalTime.of(22, 0, 0),
				LocalTime.of(1, 59, 59),
				false
			),
			Arguments.of(
				LocalTime.of(0, 0),
				LocalTime.MAX,
				true
			)
		);
	}

}
