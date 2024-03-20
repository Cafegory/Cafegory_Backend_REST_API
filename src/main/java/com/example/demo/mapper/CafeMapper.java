package com.example.demo.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.domain.BusinessHour;
import com.example.demo.domain.CafeImpl;
import com.example.demo.domain.CafeSearchCondition;
import com.example.demo.domain.OpenChecker;
import com.example.demo.dto.BusinessHourResponse;
import com.example.demo.dto.CafeBasicInfoResponse;
import com.example.demo.dto.CafeResponse;
import com.example.demo.dto.CafeSearchRequest;
import com.example.demo.dto.CafeSearchResponse;
import com.example.demo.dto.CanMakeStudyOnceResponse;
import com.example.demo.dto.ReviewResponse;
import com.example.demo.dto.SnsResponse;
import com.example.demo.dto.StudyOnceForCafeResponse;

public class CafeMapper {

	public List<CafeSearchResponse> toCafeSearchResponses(List<CafeImpl> cafes,
		OpenChecker<BusinessHour> openChecker) {
		return cafes.stream()
			.map(cafe ->
				produceCafeSearchResponse(cafe, openChecker)
			)
			.collect(Collectors.toList());
	}

	private CafeSearchResponse produceCafeSearchResponse(CafeImpl cafe, OpenChecker<BusinessHour> openChecker) {
		return new CafeSearchResponse(
			cafe.getId(),
			cafe.getName(),
			cafe.showFullAddress(),
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

	public CafeSearchResponse toCafeSearchResponse(CafeImpl cafe, OpenChecker<BusinessHour> openChecker) {
		return produceCafeSearchResponse(cafe, openChecker);
	}

	public CafeBasicInfoResponse toCafeBasicInfoResponse(CafeImpl cafe,
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

	public CafeResponse toCafeResponse(
		CafeImpl findCafe,
		List<BusinessHourResponse> businessHourResponses,
		List<SnsResponse> snsResponses,
		List<ReviewResponse> reviewResponses,
		List<StudyOnceForCafeResponse> studyOnceForCafeResponses,
		OpenChecker<BusinessHour> openChecker) {
		return CafeResponse.builder()
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

	public CafeResponse toCafeResponseWithEmptyInfo(
		CafeImpl findCafe,
		List<BusinessHourResponse> businessHourResponses,
		List<SnsResponse> snsResponses,
		List<ReviewResponse> reviewResponses,
		OpenChecker<BusinessHour> openChecker
	) {
		return CafeResponse.builder()
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
