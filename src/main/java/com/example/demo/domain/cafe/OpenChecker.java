package com.example.demo.domain.cafe;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface OpenChecker<T> {

	boolean checkByNowTime(DayOfWeek dayOfWeek, LocalTime businessStartTime, LocalTime businessEndTime,
		LocalDateTime now);

	boolean checkWithBusinessHours(List<T> hours, LocalDateTime now);

}
