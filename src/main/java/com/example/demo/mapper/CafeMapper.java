package com.example.demo.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.domain.cafe.BusinessHour;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.cafe.CafeSearchCondition;
import com.example.demo.domain.cafe.OpenChecker;
import com.example.demo.dto.cafe.BusinessHourResponse;
import com.example.demo.dto.cafe.CafeSearchBasicInfoResponse;
import com.example.demo.dto.cafe.CafeSearchBusinessHourResponse;
import com.example.demo.dto.cafe.CafeSearchListRequest;
import com.example.demo.dto.cafe.CafeSearchListResponse;
import com.example.demo.dto.cafe.CafeSearchResponse;
import com.example.demo.dto.cafe.CafeSearchSnsResponse;
import com.example.demo.dto.cafe.CafeSearchStudyOnceResponse;
import com.example.demo.dto.cafe.SnsResponse;
import com.example.demo.dto.study.CanMakeStudyOnceResponse;

public class CafeMapper {

	public List<CafeSearchListResponse> toCafeSearchListResponses(List<Cafe> cafes,
		OpenChecker<BusinessHour> openChecker) {
		return cafes.stream()
			.map(cafe ->
				produceCafeSearchListResponse(cafe, openChecker)
			)
			.collect(Collectors.toList());
	}

	private CafeSearchListResponse produceCafeSearchListResponse(Cafe cafe, OpenChecker<BusinessHour> openChecker) {
		return new CafeSearchListResponse(
			cafe.getId(),
			cafe.getName(),
			cafe.showFullAddress(),
			cafe.getRegion(),
			cafe.getBusinessHours().stream()
				.map(hour -> new BusinessHourResponse(hour.getDay(), hour.getStartTime().toString(),
					hour.getEndTime().toString()))
				.collect(Collectors.toList()),
			cafe.isOpen(openChecker),
			cafe.getSnsDetails().stream()
				.map(s -> new SnsResponse(s.getName(), s.getUrl()))
				.collect(Collectors.toList()),
			cafe.getPhone(),
			cafe.getMinBeveragePrice(),
			cafe.getMaxAllowableStay().getValue(),
			cafe.calcAverageRating().orElse(0),
			cafe.isAbleToStudy()
		);
	}

	public CafeSearchListResponse toCafeSearchResponse(Cafe cafe, OpenChecker<BusinessHour> openChecker) {
		return produceCafeSearchListResponse(cafe, openChecker);
	}

	public CafeSearchBasicInfoResponse toCafeSearchBasicInfoResponse(Cafe cafe,
		List<CafeSearchBusinessHourResponse> cafeSearchBusinessHourResponses,
		List<CafeSearchSnsResponse> cafeSearchSnsResponses,
		OpenChecker<BusinessHour> openChecker) {
		return new CafeSearchBasicInfoResponse(
			cafe.getId(),
			cafe.getName(),
			cafe.showFullAddress(),
			cafeSearchBusinessHourResponses,
			cafe.isOpen(openChecker),
			cafeSearchSnsResponses,
			cafe.getPhone(),
			cafe.getMinBeveragePrice(),
			cafe.getMaxAllowableStay().getValue(),
			cafe.calcAverageRating().orElse(0),
			cafe.isAbleToStudy()
		);
	}

	public CafeSearchCondition toCafeSearchCondition(CafeSearchListRequest request) {
		return new CafeSearchCondition.Builder(request.isCanStudy(),
			request.getArea())
			.maxTime(request.getMaxTime())
			.minMenuPrice(request.getMinBeveragePrice())
			.startTime(request.getStartTime())
			.endTime(request.getEndTime())
			.build();
	}

	public CafeSearchResponse toCafeSearchResponse(
		Cafe findCafe,
		List<CafeSearchBusinessHourResponse> cafeSearchBusinessHourResponses,
		List<CafeSearchSnsResponse> cafeSearchSnsResponses,
		List<CafeSearchStudyOnceResponse> cafeSearchStudyOnceResponses,
		OpenChecker<BusinessHour> openChecker) {
		return CafeSearchResponse.builder()
			.basicInfo(
				toCafeSearchBasicInfoResponse(
					findCafe,
					cafeSearchBusinessHourResponses,
					cafeSearchSnsResponses,
					openChecker)
			)
			.totalElementsOfReview(
				findCafe.getReviews().size()
			)
			.meetings(
				cafeSearchStudyOnceResponses
			)
			.canMakeMeeting(
				List.of(new CanMakeStudyOnceResponse(), new CanMakeStudyOnceResponse())
			)
			.build();
	}

	public CafeSearchResponse toCafeSearchResponseWithEmptyInfo(
		Cafe findCafe,
		List<CafeSearchBusinessHourResponse> businessHourResponses,
		List<CafeSearchSnsResponse> snsResponses,
		OpenChecker<BusinessHour> openChecker
	) {
		return CafeSearchResponse.builder()
			.basicInfo(
				toCafeSearchBasicInfoResponse(
					findCafe,
					businessHourResponses,
					snsResponses,
					openChecker)
			)
			.totalElementsOfReview(
				findCafe.getReviews().size()
			)
			.meetings(
				Collections.emptyList()
			)
			.canMakeMeeting(
				Collections.emptyList()
			)
			.build();
	}

}
