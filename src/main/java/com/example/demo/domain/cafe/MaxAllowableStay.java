package com.example.demo.domain.cafe;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public enum MaxAllowableStay {

	IRRELEVANT(0, maxHour -> false),
	ONE_HOUR(1, maxHour -> isLoeOrZeroByMaxHour(1, maxHour)),
	TWO_HOUR(2, maxHour -> isLoeOrZeroByMaxHour(2, maxHour)),
	THREE_HOUR(3, maxHour -> isLoeOrZeroByMaxHour(3, maxHour)),
	FOUR_HOUR(4, maxHour -> isLoeOrZeroByMaxHour(4, maxHour)),
	FIVE_HOUR(5, maxHour -> isLoeOrZeroByMaxHour(5, maxHour)),
	SIX_HOUR(6, maxHour -> isLoeOrZeroByMaxHour(6, maxHour)),
	OVER_SIX_HOUR(7, maxHour -> equals(7, maxHour) || isZero(maxHour));

	private final int value;
	private final Predicate<Integer> predicate;

	MaxAllowableStay(int value, Predicate<Integer> predicate) {
		this.value = value;
		this.predicate = predicate;
	}

	public static MaxAllowableStay find(int maxTime) {
		return Arrays.stream(values())
			.filter(hour -> hour.value == maxTime)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("최대 이용가능시간을 찾을 수 없습니다."));
	}

	public static List<MaxAllowableStay> findLoe(MaxAllowableStay maxAllowableStay) {
		return Arrays.stream(values())
			.filter(hour -> hour.predicate.test(maxAllowableStay.value))
			.collect(Collectors.toList());
	}

	private static boolean isLoeOrZeroByMaxHour(int value, Integer maxHour) {
		return isLoeHour(value, maxHour) || isZero(maxHour);
	}

	private static boolean isZero(int hour) {
		return hour == 0;
	}

	private static boolean isLoeHour(int value, int hour) {
		return value <= hour;
	}

	private static boolean equals(int value, int hour) {
		return value == hour;
	}
}
