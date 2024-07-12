package com.example.demo.service.cafe;

import static com.example.demo.exception.ExceptionType.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.cafe.BusinessHour;
import com.example.demo.domain.cafe.BusinessHourOpenChecker;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.cafe.CafeSearchCondition;
import com.example.demo.domain.cafe.OpenChecker;
import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.cafe.CafeSearchListRequest;
import com.example.demo.dto.cafe.CafeSearchListResponse;
import com.example.demo.dto.cafe.CafeSearchResponse;
import com.example.demo.exception.CafegoryException;
import com.example.demo.mapper.BusinessHourMapper;
import com.example.demo.mapper.CafeMapper;
import com.example.demo.mapper.ReviewMapper;
import com.example.demo.mapper.SnsDetailMapper;
import com.example.demo.mapper.StudyOnceMapper;
import com.example.demo.repository.cafe.CafeQueryDslRepository;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.util.PageRequestCustom;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeServiceImpl implements CafeService {

	private final CafeQueryDslRepository cafeQueryDslRepository;
	private final CafeRepository cafeRepository;
	private final MemberRepository memberRepository;
	private final OpenChecker<BusinessHour> openChecker = new BusinessHourOpenChecker();
	private final BusinessHourMapper businessHourMapper;
	private final SnsDetailMapper snsDetailMapper;
	private final CafeMapper cafeMapper;
	private final ReviewMapper reviewMapper;
	private final StudyOnceMapper studyOnceMapper;

	@Override
	public PagedResponse<CafeSearchListResponse> searchWithPagingByDynamicFilter(CafeSearchListRequest request) {
		Pageable pageable = PageRequestCustom.of(request.getPage(), request.getSizePerPage());
		CafeSearchCondition cafeSearchCondition = cafeMapper.toCafeSearchCondition(request);

		Page<Cafe> pagedCafes = cafeQueryDslRepository.findWithDynamicFilter(cafeSearchCondition,
			pageable);
		return createPagedResponse(pagedCafes,
			cafeMapper.toCafeSearchListResponses(pagedCafes.getContent(), openChecker));
	}

	@Override
	public CafeSearchListResponse searchCafeBasicInfoById(Long cafeId) {
		return cafeMapper.toCafeSearchResponse(findCafeById(cafeId), openChecker);
	}

	private Cafe findCafeById(Long cafeId) {
		return cafeRepository.findById(cafeId)
			.orElseThrow(() -> new CafegoryException(CAFE_NOT_FOUND));
	}

	@Override
	public CafeSearchResponse searchCafeForMemberByCafeId(Long cafeId, Long memberId) {
		Cafe findCafe = findCafeById(cafeId);
		if (!memberRepository.existsById(memberId)) {
			throw new CafegoryException(MEMBER_NOT_FOUND);
		}
		return cafeMapper.toCafeSearchResponse(
			findCafe,
			businessHourMapper.toCafeSearchBusinessHourResponses(findCafe.getBusinessHours()),
			snsDetailMapper.toCafeSearchSnsResponses(findCafe.getSnsDetails()),
			studyOnceMapper.toCafeSearchStudyOnceResponse(findCafe),
			openChecker
		);
	}

	@Override
	public CafeSearchResponse searchCafeForNotMemberByCafeId(Long cafeId) {
		Cafe findCafe = findCafeById(cafeId);
		return cafeMapper.toCafeSearchResponseWithEmptyInfo(
			findCafe,
			businessHourMapper.toCafeSearchBusinessHourResponses(findCafe.getBusinessHours()),
			snsDetailMapper.toCafeSearchSnsResponses(findCafe.getSnsDetails()),
			openChecker
		);
	}

	private PagedResponse<CafeSearchListResponse> createPagedResponse(Page<Cafe> pagedCafes,
		List<CafeSearchListResponse> cafeSearchListResponse) {
		return PagedResponse.createWithFirstPageAsOne(
			pagedCafes.getNumber(),
			pagedCafes.getTotalPages(),
			pagedCafes.getNumberOfElements(),
			pagedCafes.getTotalElements(),
			cafeSearchListResponse
		);
	}

}
