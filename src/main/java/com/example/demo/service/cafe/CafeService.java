package com.example.demo.service.cafe;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.cafe.CafeResponse;
import com.example.demo.dto.cafe.CafeSearchRequest;
import com.example.demo.dto.cafe.CafeSearchResponse;

public interface CafeService {

	PagedResponse<CafeSearchResponse> searchWithPagingByDynamicFilter(CafeSearchRequest request);

	CafeSearchResponse searchCafeBasicInfoById(Long cafeId);

	CafeResponse searchCafeForMemberByCafeId(Long cafeId, Long memberId);

	CafeResponse searchCafeForNotMemberByCafeId(Long cafeId);

}
