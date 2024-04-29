package com.example.demo.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.domain.cafe.BusinessHour;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.cafe.CafeSearchCondition;
import com.example.demo.domain.cafe.OpenChecker;
import com.example.demo.dto.cafe.BusinessHourResponse;
import com.example.demo.dto.cafe.CafeBasicInfoResponse;
import com.example.demo.dto.cafe.CafeSearchListResponse;
import com.example.demo.dto.cafe.CafeSearchRequest;
import com.example.demo.dto.cafe.CafeSearchResponse;
import com.example.demo.dto.cafe.SnsResponse;
import com.example.demo.dto.review.ReviewResponse;
import com.example.demo.dto.study.CanMakeStudyOnceResponse;
import com.example.demo.dto.study.StudyOnceForCafeResponse;

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
			cafe.getAvgReviewRate()
		);
	}

	public CafeSearchListResponse toCafeSearchResponse(Cafe cafe, OpenChecker<BusinessHour> openChecker) {
		return produceCafeSearchListResponse(cafe, openChecker);
	}

	public CafeBasicInfoResponse toCafeBasicInfoResponse(Cafe cafe,
		List<BusinessHourResponse> businessHourResponses,
		List<SnsResponse> snsResponses,
		OpenChecker<BusinessHour> openChecker) {
		return new CafeBasicInfoResponse(
			cafe.getId(),
			cafe.getName(),
			cafe.showFullAddress(),
			businessHourResponses,
			cafe.isOpen(openChecker),
			snsResponses,
			cafe.getPhone(),
			cafe.getMinBeveragePrice(),
			cafe.getMaxAllowableStay().getValue(),
			cafe.getAvgReviewRate()
		);
	}

	public CafeSearchCondition toCafeSearchCondition(CafeSearchRequest request) {
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
		List<BusinessHourResponse> businessHourResponses,
		List<SnsResponse> snsResponses,
		List<ReviewResponse> reviewResponses,
		List<StudyOnceForCafeResponse> studyOnceForCafeResponses,
		OpenChecker<BusinessHour> openChecker) {
		return CafeSearchResponse.builder()
			.basicInfo(
				toCafeBasicInfoResponse(
					findCafe,
					businessHourResponses,
					snsResponses,
					openChecker)
			)
			.review(
				reviewResponses
			)
			.meetings(
				studyOnceForCafeResponses
			)
			.canMakeMeeting(
				List.of(new CanMakeStudyOnceResponse(), new CanMakeStudyOnceResponse())
			)
			.build();
	}

	public CafeSearchResponse toCafeSearchResponseWithEmptyInfo(
		Cafe findCafe,
		List<BusinessHourResponse> businessHourResponses,
		List<SnsResponse> snsResponses,
		List<ReviewResponse> reviewResponses,
		OpenChecker<BusinessHour> openChecker
	) {
		return CafeSearchResponse.builder()
			.basicInfo(
				toCafeBasicInfoResponse(
					findCafe,
					businessHourResponses,
					snsResponses,
					openChecker)
			)
			.review(
				reviewResponses
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
