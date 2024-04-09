package com.example.demo.dto.cafe;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CafeSearchResponse {

	private final long cafeId;
	private final String name;
	private final String address;
	private final String area;
	private final List<BusinessHourResponse> businessHours;
	private final Boolean isOpen;
	private final List<SnsResponse> sns;
	private final String phone;
	private final int minBeveragePrice;
	private final int maxTime;
	private final double avgReviewRate;

}
