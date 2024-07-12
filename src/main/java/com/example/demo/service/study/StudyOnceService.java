package com.example.demo.service.study;

import java.time.LocalDateTime;

import com.example.demo.domain.study.Attendance;
import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.study.StudyMemberListResponse;
import com.example.demo.dto.study.StudyOnceCreateRequest;
import com.example.demo.dto.study.StudyOnceCreateResponse;
import com.example.demo.dto.study.StudyOnceResponse;
import com.example.demo.dto.study.StudyOnceSearchListResponse;
import com.example.demo.dto.study.StudyOnceSearchRequest;
import com.example.demo.dto.study.StudyOnceSearchResponse;
import com.example.demo.dto.study.StudyOnceUpdateRequest;
import com.example.demo.dto.study.UpdateAttendanceRequest;
import com.example.demo.dto.study.UpdateAttendanceResponse;

public interface StudyOnceService {
	void tryJoin(long memberIdThatExpectedToJoin, long studyId);

	void tryQuit(long memberIdThatExpectedToQuit, long studyId);

	PagedResponse<StudyOnceSearchListResponse> searchStudy(StudyOnceSearchRequest studyOnceSearchRequest);

	StudyOnceSearchResponse searchByStudyId(long studyId);

	StudyOnceSearchResponse searchStudyOnceWithMemberParticipation(long studyOnceId, long memberId);

	UpdateAttendanceResponse updateAttendances(long leaderId, long studyOnceId,
		UpdateAttendanceRequest request, LocalDateTime now);

	void updateAttendance(long leaderId, long studyOnceId, long memberId, Attendance attendance, LocalDateTime now);

	StudyOnceCreateResponse createStudy(long leaderId, StudyOnceCreateRequest studyOnceCreateRequest);

	Long changeCafe(Long requestMemberId, Long studyOnceId, Long changingCafeId);

	StudyMemberListResponse findStudyMembersById(Long studyOnceId);

	StudyOnceResponse findStudyOnce(Long studyOnceId, LocalDateTime now);

	boolean doesOnlyStudyLeaderExist(Long studyOnceId);

	boolean isStudyOnceLeader(Long memberId, Long studyOnceId);

	void updateStudyOnce(long requestedMemberId, long studyOnceId, StudyOnceUpdateRequest request, LocalDateTime now);

	void updateStudyOncePartially(long requestedMemberId, long studyOnceId, StudyOnceUpdateRequest request);
}
