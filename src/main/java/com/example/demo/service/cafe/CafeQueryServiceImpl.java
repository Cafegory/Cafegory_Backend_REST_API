package com.example.demo.service.cafe;

import static com.example.demo.exception.ExceptionType.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.cafe.BusinessHour;
import com.example.demo.domain.cafe.BusinessHourOpenChecker;
import com.example.demo.domain.cafe.CafeImpl;
import com.example.demo.domain.cafe.CafeSearchCondition;
import com.example.demo.domain.cafe.OpenChecker;
import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.cafe.CafeResponse;
import com.example.demo.dto.cafe.CafeSearchRequest;
import com.example.demo.dto.cafe.CafeSearchResponse;
import com.example.demo.exception.CafegoryException;
import com.example.demo.mapper.BusinessHourMapper;
import com.example.demo.mapper.CafeMapper;
import com.example.demo.mapper.ReviewMapper;
import com.example.demo.mapper.SnsDetailMapper;
import com.example.demo.mapper.StudyOnceMapper;
import com.example.demo.repository.cafe.CafeQueryRepository;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.util.PageRequestCustom;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeQueryServiceImpl implements CafeQueryService {

	private final CafeQueryRepository cafeQueryRepository;
	private final CafeRepository cafeRepository;
	private final MemberRepository memberRepository;
	private OpenChecker<BusinessHour> openChecker = new BusinessHourOpenChecker();
	private final BusinessHourMapper businessHourMapper;
	private final SnsDetailMapper snsDetailMapper;
	private final CafeMapper cafeMapper;
	private final ReviewMapper reviewMapper;
	private final StudyOnceMapper studyOnceMapper;

	@Override
	public PagedResponse<CafeSearchResponse> searchWithPagingByDynamicFilter(CafeSearchRequest request) {
		Pageable pageable = PageRequestCustom.of(request.getPage(), request.getSizePerPage());
		CafeSearchCondition cafeSearchCondition = cafeMapper.toCafeSearchCondition(request);

		Page<CafeImpl> pagedCafes = cafeQueryRepository.findWithDynamicFilter(cafeSearchCondition,
			pageable);
		return createPagedResponse(pagedCafes,
			cafeMapper.toCafeSearchResponses(pagedCafes.getContent(), openChecker));
	}

	@Override
	public CafeResponse searchCafeById(Long cafeId) {
		CafeImpl findCafe = findCafeById(cafeId);
		return cafeMapper.toCafeResponse(
			findCafe,
			businessHourMapper.toBusinessHourResponses(findCafe.getBusinessHours()),
			snsDetailMapper.toSnsResponses(findCafe.getSnsDetails()),
			reviewMapper.toReviewResponses(findCafe.getReviews()),
			studyOnceMapper.toStudyOnceForCafeResponse(findCafe),
			openChecker
		);
	}

	@Override
	public CafeSearchResponse searchCafeBasicInfoById(Long cafeId) {
		return cafeMapper.toCafeSearchResponse(findCafeById(cafeId), openChecker);
	}

	private CafeImpl findCafeById(Long cafeId) {
		return cafeRepository.findById(cafeId)
			.orElseThrow(() -> new CafegoryException(CAFE_NOT_FOUND));
	}

	@Override
	public CafeResponse searchCafeForMemberByCafeId(Long cafeId, Long memberId) {
		CafeImpl findCafe = findCafeById(cafeId);
		if (!memberRepository.existsById(memberId)) {
			throw new CafegoryException(MEMBER_NOT_FOUND);
		}
		return cafeMapper.toCafeResponse(
			findCafe,
			businessHourMapper.toBusinessHourResponses(findCafe.getBusinessHours()),
			snsDetailMapper.toSnsResponses(findCafe.getSnsDetails()),
			reviewMapper.toReviewResponses(findCafe.getReviews()),
			studyOnceMapper.toStudyOnceForCafeResponse(findCafe),
			openChecker
		);
	}

	@Override
	public CafeResponse searchCafeForNotMemberByCafeId(Long cafeId) {
		CafeImpl findCafe = findCafeById(cafeId);
		return cafeMapper.toCafeResponseWithEmptyInfo(
			findCafe,
			businessHourMapper.toBusinessHourResponses(findCafe.getBusinessHours()),
			snsDetailMapper.toSnsResponses(findCafe.getSnsDetails()),
			reviewMapper.toReviewResponses(findCafe.getReviews()),
			openChecker
		);
	}

	private PagedResponse<CafeSearchResponse> createPagedResponse(Page<CafeImpl> pagedCafes,
		List<CafeSearchResponse> cafeSearchResponses) {
		return PagedResponse.createWithFirstPageAsOne(
			pagedCafes.getNumber(),
			pagedCafes.getTotalPages(),
			pagedCafes.getNumberOfElements(),
			cafeSearchResponses
		);
	}

}
