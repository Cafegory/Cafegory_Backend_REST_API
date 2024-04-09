package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.domain.cafe.BusinessHour;
import com.example.demo.dto.cafe.BusinessHourResponse;

public class BusinessHourMapper {

	public List<BusinessHourResponse> toBusinessHourResponses(List<BusinessHour> businessHours) {
		return businessHours.stream()
			.map(hour -> new BusinessHourResponse(hour.getDay(), hour.getStartTime().toString(),
				hour.getEndTime().toString()))
			.collect(Collectors.toList());
	}

}
