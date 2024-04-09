package com.example.demo.builder;

import com.example.demo.domain.cafe.Address;
import com.example.demo.domain.cafe.CafeImpl;
import com.example.demo.domain.cafe.MaxAllowableStay;

public class TestCafeBuilder {

	private Long id;
	private String name = "카페고리";
	private Address address = new Address("서울 마포구 합정동", "합정동");
	private String phone = "010-1234-5678";
	private MaxAllowableStay maxAllowableStay = MaxAllowableStay.THREE_HOUR;
	private double avgReviewRate = 4.5;
	private boolean isAbleToStudy = true;
	private int minBeveragePrice = 3_000;

	public TestCafeBuilder id(Long id) {
		this.id = id;
		return this;
	}

	public TestCafeBuilder name(String name) {
		this.name = name;
		return this;
	}

	public TestCafeBuilder address(Address address) {
		this.address = address;
		return this;
	}

	public TestCafeBuilder phone(String phone) {
		this.phone = phone;
		return this;
	}

	public TestCafeBuilder maxAllowableStay(MaxAllowableStay maxAllowableStay) {
		this.maxAllowableStay = maxAllowableStay;
		return this;
	}

	public TestCafeBuilder avgReviewRate(double avgReviewRate) {
		this.avgReviewRate = avgReviewRate;
		return this;
	}

	public TestCafeBuilder isNotAbleToStudy() {
		this.isAbleToStudy = false;
		return this;
	}

	public TestCafeBuilder minBeveragePrice(int minBeveragePrice) {
		this.minBeveragePrice = minBeveragePrice;
		return this;
	}

	public CafeImpl build() {
		return CafeImpl.builder()
			.id(id)
			.name(name)
			.address(address)
			.phone(phone)
			.maxAllowableStay(maxAllowableStay)
			.avgReviewRate(avgReviewRate)
			.isAbleToStudy(isAbleToStudy)
			.minBeveragePrice(minBeveragePrice)
			.build();
	}
}
