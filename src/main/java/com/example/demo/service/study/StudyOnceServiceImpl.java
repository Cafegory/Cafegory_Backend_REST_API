package com.example.demo.service.study;

import static com.example.demo.exception.ExceptionType.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.Attendance;
import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyMemberId;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.member.MemberResponse;
import com.example.demo.dto.study.StudyMemberStateRequest;
import com.example.demo.dto.study.StudyMemberStateResponse;
import com.example.demo.dto.study.StudyMembersResponse;
import com.example.demo.dto.study.StudyOnceCreateRequest;
import com.example.demo.dto.study.StudyOnceSearchRequest;
import com.example.demo.dto.study.StudyOnceSearchResponse;
import com.example.demo.dto.study.UpdateAttendanceRequest;
import com.example.demo.dto.study.UpdateAttendanceResponse;
import com.example.demo.exception.CafegoryException;
import com.example.demo.mapper.StudyMemberMapper;
import com.example.demo.mapper.StudyOnceMapper;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.study.StudyMemberRepository;
import com.example.demo.repository.study.StudyOnceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyOnceServiceImpl implements StudyOnceService {

	private final CafeRepository cafeRepository;
	private final StudyOnceRepository studyOnceRepository;
	private final MemberRepository memberRepository;
	private final StudyMemberRepository studyMemberRepository;
	private final StudyOnceMapper studyOnceMapper;
	private final StudyMemberMapper studyMemberMapper;

	@Override
	public void tryJoin(long memberIdThatExpectedToJoin, long studyId) {
		StudyOnce studyOnce = studyOnceRepository.findById(studyId)
			.orElseThrow(() -> new CafegoryException(STUDY_ONCE_NOT_FOUND));
		LocalDateTime startDateTime = studyOnce.getStartDateTime();
		Member member = getMember(memberIdThatExpectedToJoin, startDateTime);
		studyOnce.tryJoin(member, LocalDateTime.now());
	}

	@Override
	public void tryQuit(long memberIdThatExpectedToQuit, long studyId) {
		Member member = memberRepository.findById(memberIdThatExpectedToQuit)
			.orElseThrow(() -> new CafegoryException(MEMBER_NOT_FOUND));
		StudyOnce studyOnce = studyOnceRepository.findById(studyId)
			.orElseThrow(() -> new CafegoryException(STUDY_ONCE_NOT_FOUND));
		if (studyOnce.getLeader().equals(member)) {
			deleteStudy(studyOnce);
		}
		StudyMember needToRemoveStudyMember = studyOnce.tryQuit(member, LocalDateTime.now());
		studyMemberRepository.delete(needToRemoveStudyMember);
	}

	private void deleteStudy(StudyOnce studyOnce) {
		if (studyOnce.getStudyMembers().size() > 1) {
			throw new CafegoryException(STUDY_ONCE_LEADER_QUIT_FAIL);
		}
		studyOnceRepository.delete(studyOnce);
	}

	@Override
	public PagedResponse<StudyOnceSearchResponse> searchStudy(StudyOnceSearchRequest studyOnceSearchRequest) {
		int totalCount = Math.toIntExact(studyOnceRepository.count(studyOnceSearchRequest));
		int sizePerPage = studyOnceSearchRequest.getSizePerPage();
		int maxPage = calculateMaxPage(totalCount, sizePerPage);
		List<StudyOnceSearchResponse> searchResults = studyOnceRepository.findAllByStudyOnceSearchRequest(
				studyOnceSearchRequest)
			.stream()
			.map(studyOnce -> studyOnceMapper.toStudyOnceSearchResponse(studyOnce,
				studyOnce.canJoin(LocalDateTime.now())))
			.collect(Collectors.toList());
		return new PagedResponse<>(studyOnceSearchRequest.getPage(), maxPage, searchResults.size(), searchResults);
	}

	private int calculateMaxPage(int totalCount, int sizePerPage) {
		if (totalCount % sizePerPage == 0) {
			return totalCount / sizePerPage;
		}
		return totalCount / sizePerPage + 1;
	}

	@Override
	public StudyOnceSearchResponse searchByStudyId(long studyId) {
		StudyOnce searched = studyOnceRepository.findById(studyId)
			.orElseThrow(() -> new CafegoryException(STUDY_ONCE_NOT_FOUND));
		boolean canJoin = searched.canJoin(LocalDateTime.now());
		return studyOnceMapper.toStudyOnceSearchResponse(searched, canJoin);
	}

	@Override
	public UpdateAttendanceResponse updateAttendances(long leaderId, long studyOnceId,
		UpdateAttendanceRequest request, LocalDateTime now) {
		processAttendanceUpdates(leaderId, studyOnceId, request, now);

		List<StudyMemberId> studyMemberIds = generateStudyMemberIdsFromRequest(studyOnceId, request);
		List<StudyMember> studyMembers = studyMemberRepository.findAllById(studyMemberIds);
		return new UpdateAttendanceResponse(mapToStateResponses(studyMembers));
	}

	private List<StudyMemberStateResponse> mapToStateResponses(List<StudyMember> studyMembers) {
		return studyMembers.stream()
			.map(studyMember -> new StudyMemberStateResponse(studyMember.getId().getMemberId(),
				studyMember.getAttendance().isPresent(), studyMember.getLastModifiedDate()))
			.collect(Collectors.toList());
	}

	private List<StudyMemberId> generateStudyMemberIdsFromRequest(long studyOnceId, UpdateAttendanceRequest request) {
		return request.getStates().stream()
			.map(memberReq -> new StudyMemberId(memberReq.getMemberId(), studyOnceId))
			.collect(Collectors.toList());
	}

	private void processAttendanceUpdates(long leaderId, long studyOnceId, UpdateAttendanceRequest request,
		LocalDateTime now) {
		for (StudyMemberStateRequest memberStateRequest : request.getStates()) {
			Attendance attendance = memberStateRequest.isAttendance() ? Attendance.YES : Attendance.NO;
			updateAttendance(leaderId, studyOnceId, memberStateRequest.getMemberId(), attendance, now);
		}
	}

	@Override
	public void updateAttendance(long leaderId, long studyOnceId, long memberId, Attendance attendance,
		LocalDateTime now) {
		if (!studyOnceRepository.existsByLeaderId(leaderId)) {
			throw new CafegoryException(STUDY_ONCE_INVALID_LEADER);
		}

		StudyOnce searched = findStudyOnceById(studyOnceId);
		validateEarlyToTakeAttendance(now, searched.getStartDateTime());
		validateLateToTakeAttendance(now, searched.getStartDateTime(), searched.getEndDateTime());

		StudyMember findStudyMember = studyMemberRepository.findById(new StudyMemberId(memberId, studyOnceId))
			.orElseThrow(() -> new CafegoryException(STUDY_MEMBER_NOT_FOUND));
		findStudyMember.setAttendance(attendance);
		findStudyMember.setLastModifiedDate(now);
	}

	private void validateLateToTakeAttendance(LocalDateTime now, LocalDateTime startDateTime,
		LocalDateTime endDateTime) {
		Duration halfDuration = Duration.between(startDateTime, endDateTime).dividedBy(2);
		LocalDateTime midTime = startDateTime.plus(halfDuration);

		if (now.isAfter(midTime)) {
			throw new CafegoryException(STUDY_ONCE_LATE_TAKE_ATTENDANCE);
		}
	}

	private void validateEarlyToTakeAttendance(LocalDateTime now, LocalDateTime startDateTime) {
		if (now.minusMinutes(10).isBefore(startDateTime)) {
			throw new CafegoryException(STUDY_ONCE_EARLY_TAKE_ATTENDANCE);
		}
	}

	private StudyOnce findStudyOnceById(long studyOnceId) {
		return studyOnceRepository.findById(studyOnceId)
			.orElseThrow(() -> new CafegoryException(STUDY_ONCE_NOT_FOUND));
	}

	@Override
	public StudyOnceSearchResponse createStudy(long leaderId, StudyOnceCreateRequest studyOnceCreateRequest) {
		Cafe cafe = cafeRepository.findById(studyOnceCreateRequest.getCafeId())
			.orElseThrow(() -> new CafegoryException(CAFE_NOT_FOUND));
		//ToDo 카페 영업시간 이내인지 확인 하는 작업 추가 필요
		LocalDateTime startDateTime = studyOnceCreateRequest.getStartDateTime();
		Member leader = getMember(leaderId, startDateTime);
		StudyOnce studyOnce = studyOnceMapper.toNewEntity(studyOnceCreateRequest, cafe, leader);
		StudyOnce saved = studyOnceRepository.save(studyOnce);
		boolean canJoin = true;
		return studyOnceMapper.toStudyOnceSearchResponse(saved, canJoin);
	}

	private Member getMember(long leaderId, LocalDateTime startDateTime) {
		Member leader = memberRepository.findById(leaderId)
			.orElseThrow(() -> new CafegoryException(MEMBER_NOT_FOUND));
		var studyMembers = studyMemberRepository.findByMemberAndStudyDate(leader, startDateTime.toLocalDate());
		leader.setStudyMembers(studyMembers);
		return leader;
	}

	@Override
	public Long changeCafe(Long requestMemberId, Long studyOnceId, final Long changingCafeId) {
		final StudyOnce studyOnce = findStudyOnceById(studyOnceId);
		if (!isStudyOnceLeader(requestMemberId, studyOnceId)) {
			throw new CafegoryException(STUDY_ONCE_LOCATION_CHANGE_PERMISSION_DENIED);
		}
		studyOnce.changeCafe(findCafeById(changingCafeId));
		return changingCafeId;
	}

	@Override
	public StudyMembersResponse findStudyMembersById(Long studyOnceId) {
		StudyOnce studyOnce = findStudyOnceById(studyOnceId);
		List<MemberResponse> memberResponses = studyMemberMapper.toMemberResponses(studyOnce.getStudyMembers());
		return new StudyMembersResponse(memberResponses);
	}

	@Override
	public boolean isStudyOnceLeader(Long memberId, Long studyOnceId) {
		StudyOnce studyOnce = findStudyOnceById(studyOnceId);
		return studyOnce.isLeader(findMemberById(memberId));
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new CafegoryException(MEMBER_NOT_FOUND));
	}

	private Cafe findCafeById(Long cafeId) {
		return cafeRepository.findById(cafeId)
			.orElseThrow(() -> new CafegoryException(CAFE_NOT_FOUND));
	}
}
