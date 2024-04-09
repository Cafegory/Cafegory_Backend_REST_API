package com.example.demo.service.study;

import java.time.LocalDateTime;

import com.example.demo.domain.study.Attendance;
import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.study.StudyMembersResponse;
import com.example.demo.dto.study.StudyOnceCreateRequest;
import com.example.demo.dto.study.StudyOnceSearchRequest;
import com.example.demo.dto.study.StudyOnceSearchResponse;
import com.example.demo.dto.study.UpdateAttendanceRequest;
import com.example.demo.dto.study.UpdateAttendanceResponse;

public interface StudyOnceService {
	void tryJoin(long memberIdThatExpectedToJoin, long studyId);

	void tryQuit(long memberIdThatExpectedToQuit, long studyId);

	PagedResponse<StudyOnceSearchResponse> searchStudy(StudyOnceSearchRequest studyOnceSearchRequest);

	StudyOnceSearchResponse searchByStudyId(long studyId);

	UpdateAttendanceResponse updateAttendances(long leaderId, long studyOnceId,
		UpdateAttendanceRequest request, LocalDateTime now);

	void updateAttendance(long leaderId, long studyOnceId, long memberId, Attendance attendance, LocalDateTime now);

	StudyOnceSearchResponse createStudy(long leaderId, StudyOnceCreateRequest studyOnceCreateRequest);

	Long changeCafe(Long requestMemberId, Long studyOnceId, Long changingCafeId);

	StudyMembersResponse findStudyMembersById(Long studyOnceId);

	boolean isStudyOnceLeader(Long memberId, Long studyOnceId);
}
