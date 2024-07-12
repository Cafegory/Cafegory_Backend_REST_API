package com.example.demo.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class TruncatedTimeUtil {

	public static final LocalTime MAX_LOCAL_TIME = LocalTime.of(23, 59, 59);
	public static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

	public static LocalTime truncateTimeToSecond(LocalTime time) {
		return time == null ? null : time.truncatedTo(ChronoUnit.SECONDS);
	}

	public static LocalDateTime truncateDateTimeToSecond(LocalDateTime dateTime) {
		return dateTime == null ? null : dateTime.truncatedTo(ChronoUnit.SECONDS);
	}
}
