package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.domain.cafe.SnsDetail;
import com.example.demo.dto.SnsResponse;

public class SnsDetailMapper {

	public List<SnsResponse> toSnsResponses(List<SnsDetail> snsDetails) {
		return snsDetails.stream()
			.map(snsDetail -> new SnsResponse(snsDetail.getName(), snsDetail.getUrl()))
			.collect(Collectors.toList());
	}
}
