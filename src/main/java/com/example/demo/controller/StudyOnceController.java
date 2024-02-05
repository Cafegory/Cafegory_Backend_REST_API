package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.auth.CafegoryTokenManager;
import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.StudyOnceCreateRequest;
import com.example.demo.dto.StudyOnceJoinResult;
import com.example.demo.dto.StudyOnceSearchRequest;
import com.example.demo.dto.StudyOnceSearchResponse;
import com.example.demo.service.StudyOnceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/study/once")
@RequiredArgsConstructor
public class StudyOnceController {
	private final StudyOnceService studyOnceService;
	private final CafegoryTokenManager cafegoryTokenManager;

	@GetMapping("/{studyId:[0-9]+}")
	public ResponseEntity<StudyOnceSearchResponse> search(@PathVariable Long studyId) {
		StudyOnceSearchResponse response = studyOnceService.searchByStudyId(studyId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/list")
	public ResponseEntity<PagedResponse<StudyOnceSearchResponse>> searchList(
		@ModelAttribute StudyOnceSearchRequest studyOnceSearchRequest) {
		PagedResponse<StudyOnceSearchResponse> pagedResponse = studyOnceService.searchStudy(studyOnceSearchRequest);
		return ResponseEntity.ok(pagedResponse);
	}

	@PostMapping("")
	public ResponseEntity<StudyOnceSearchResponse> create(@RequestBody StudyOnceCreateRequest studyOnceCreateRequest,
		@RequestHeader("Authorization") String authorization) {
		long identityId = cafegoryTokenManager.getIdentityId(authorization);
		StudyOnceSearchResponse response = studyOnceService.createStudy(identityId, studyOnceCreateRequest);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{studyId:[0-9]+}")
	public ResponseEntity<StudyOnceJoinResult> tryJoin(@PathVariable Long studyId,
		@RequestHeader("Authorization") String authorization) {
		long identityId = cafegoryTokenManager.getIdentityId(authorization);
		LocalDateTime requestTime = LocalDateTime.now();
		studyOnceService.tryJoin(identityId, studyId);
		return ResponseEntity.ok(new StudyOnceJoinResult(requestTime, true));
	}
}
