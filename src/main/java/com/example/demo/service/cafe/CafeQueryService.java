package com.example.demo.service.cafe;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.cafe.CafeResponse;
import com.example.demo.dto.cafe.CafeSearchRequest;
import com.example.demo.dto.cafe.CafeSearchResponse;

public interface CafeQueryService {

	PagedResponse<CafeSearchResponse> searchWithPagingByDynamicFilter(CafeSearchRequest request);

	CafeResponse searchCafeById(Long cafeId);

	CafeSearchResponse searchCafeBasicInfoById(Long cafeId);

	CafeResponse searchCafeForMemberByCafeId(Long cafeId, Long memberId);

	CafeResponse searchCafeForNotMemberByCafeId(Long cafeId);

}
