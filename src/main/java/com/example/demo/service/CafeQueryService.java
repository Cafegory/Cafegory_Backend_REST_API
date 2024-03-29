package com.example.demo.service;

import com.example.demo.dto.CafeResponse;
import com.example.demo.dto.CafeSearchRequest;
import com.example.demo.dto.CafeSearchResponse;
import com.example.demo.dto.PagedResponse;

public interface CafeQueryService {

	PagedResponse<CafeSearchResponse> searchWithPagingByDynamicFilter(CafeSearchRequest request);

	CafeResponse searchCafeById(Long cafeId);

	CafeSearchResponse searchCafeBasicInfoById(Long cafeId);

	CafeResponse searchCafeForMemberByCafeId(Long cafeId, Long memberId);

	CafeResponse searchCafeForNotMemberByCafeId(Long cafeId);

}
