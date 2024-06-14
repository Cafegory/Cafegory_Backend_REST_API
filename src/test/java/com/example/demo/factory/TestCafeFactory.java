package com.example.demo.factory;

import java.util.List;

import com.example.demo.domain.cafe.Address;
import com.example.demo.domain.cafe.BusinessHour;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.cafe.MaxAllowableStay;

public class TestCafeFactory {

	public static Cafe createCafe() {
		return Cafe.builder()
			.name("카페고리")
			.address(new Address("서울 마포구 합정동", "합정동"))
			.phone("010-1234-5678")
			.maxAllowableStay(MaxAllowableStay.THREE_HOUR)
			.avgReviewRate(4.5)
			.isAbleToStudy(true)
			.minBeveragePrice(3_000)
			.build();
	}

	public static Cafe createCafeWithBusinessHours(List<BusinessHour> businessHours) {
		return Cafe.builder()
			.name("카페고리")
			.address(new Address("서울 마포구 합정동", "합정동"))
			.phone("010-1234-5678")
			.maxAllowableStay(MaxAllowableStay.THREE_HOUR)
			.avgReviewRate(4.5)
			.isAbleToStudy(true)
			.minBeveragePrice(3_000)
			.businessHours(businessHours)
			.build();
	}
}
