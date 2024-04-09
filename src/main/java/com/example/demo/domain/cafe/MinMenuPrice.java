package com.example.demo.domain.cafe;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum MinMenuPrice {

	IRRELEVANT(0, 1_000_000),
	ONE_THOUSAND_WON(1, 1_000),
	TWO_THOUSAND_WON(2, 2_000),
	THREE_THOUSAND_WON(3, 3_000),
	FOUR_THOUSAND_WON(4, 4_000),
	FIVE_THOUSAND_WON(5, 5_000),
	SIX_THOUSAND_WON(6, 6_000),
	SEVEN_THOUSAND_WON(7, 7_000),
	EIGHT_THOUSAND_WON(8, 8_000),
	NINE_THOUSAND_WON(9, 9_000),
	TEN_THOUSAND_WON(10, 10_000),
	OVER_TEN_THOUSAND_WON(11, 1_000_000);

	private final int value;
	private final int realValue;

	MinMenuPrice(int value, int realValue) {
		this.value = value;
		this.realValue = realValue;
	}

	public static MinMenuPrice find(int value) {
		return Arrays.stream(values())
			.filter(price -> price.value == value)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("최소 메뉴금액을 찾을 수 없습니다."));
	}

}
