package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.auth.CafegoryTokenManager;
import com.example.demo.dto.CafeResponse;
import com.example.demo.dto.CafeSearchRequest;
import com.example.demo.dto.CafeSearchResponse;
import com.example.demo.dto.PagedResponse;
import com.example.demo.service.CafeQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CafeController {

	private final CafeQueryService cafeQueryService;
	private final CafegoryTokenManager cafegoryTokenManager;

	@GetMapping("/cafe/list")
	public ResponseEntity<PagedResponse<CafeSearchResponse>> searchCafeList(
		@ModelAttribute CafeSearchRequest cafeSearchRequest) {
		PagedResponse<CafeSearchResponse> response = cafeQueryService.searchWithPagingByDynamicFilter(
			cafeSearchRequest);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/cafe/{cafeId}")
	public ResponseEntity<CafeResponse> searchCafe(@PathVariable Long cafeId,
		@RequestHeader(value = "Authorization", required = false) String authorization) {
		if (!StringUtils.hasText(authorization)) {
			long memberId = cafegoryTokenManager.getIdentityId(authorization);
			CafeResponse response = cafeQueryService.searchCafeForMemberByCafeId(cafeId, memberId);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		CafeResponse response = cafeQueryService.searchCafeForNotMemberByCafeId(cafeId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
