package com.example.demo.factory;

import static com.example.demo.util.TruncatedTimeUtil.*;

import java.time.LocalTime;

import com.example.demo.domain.cafe.BusinessHour;
import com.example.demo.domain.cafe.Cafe;

public class TestBusinessHourFactory {

	public static BusinessHour createBusinessHourWithDayAnd24For7(String day) {
		return BusinessHour.builder()
			.startTime(LocalTime.MIN)
			.endTime(MAX_LOCAL_TIME)
			.day(day)
			.build();
	}

	public static BusinessHour createBusinessHourWithDayAnd24For7(Cafe cafe, String day) {
		return BusinessHour.builder()
			.cafe(cafe)
			.startTime(LocalTime.MIN)
			.endTime(MAX_LOCAL_TIME)
			.day(day)
			.build();
	}

	public static BusinessHour createBusinessHourWithDayAndTime(Cafe cafe, String day, LocalTime startTime,
		LocalTime endTime) {
		return BusinessHour.builder()
			.cafe(cafe)
			.startTime(startTime)
			.endTime(endTime)
			.day(day)
			.build();
	}

	public static BusinessHour createBusinessHourWithDayAndTime(String day, LocalTime startTime, LocalTime endTime) {
		return BusinessHour.builder()
			.startTime(startTime)
			.endTime(endTime)
			.day(day)
			.build();
	}

	public static BusinessHour createBusinessHour(Long id, String day, LocalTime startTime, LocalTime endTime,
		Cafe cafe) {
		return BusinessHour.builder()
			.id(id)
			.day(day)
			.startTime(startTime)
			.endTime(endTime)
			.cafe(cafe)
			.build();
	}
}
