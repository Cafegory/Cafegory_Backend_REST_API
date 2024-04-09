package com.example.demo.service.profile;

import static com.example.demo.exception.ExceptionType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.StudyMember;
import com.example.demo.domain.StudyOnceImpl;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.dto.profile.ProfileResponse;
import com.example.demo.dto.profile.ProfileUpdateRequest;
import com.example.demo.exception.CafegoryException;
import com.example.demo.repository.StudyMemberRepository;
import com.example.demo.repository.StudyOnceRepository;
import com.example.demo.repository.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
	private final MemberRepository memberRepository;
	private final StudyOnceRepository studyOnceRepository;
	private final StudyMemberRepository studyMemberRepository;

	@Override
	public ProfileResponse get(Long requestMemberID, Long targetMemberID, LocalDateTime baseDateTime) {
		if (isOwnerOfProfile(requestMemberID, targetMemberID)) {
			return makeProfileResponse(targetMemberID);
		}
		if (isAllowedCauseStudyLeader(requestMemberID, targetMemberID)) {
			return makeProfileResponse(targetMemberID);
		}
		if (isAllowedCauseSameStudyOnceMember(requestMemberID, targetMemberID, baseDateTime)) {
			return makeProfileResponse(targetMemberID);
		}
		throw new CafegoryException(PROFILE_GET_PERMISSION_DENIED);
	}

	@Override
	public ProfileResponse update(Long requestMemberId, Long targetMemberId,
		ProfileUpdateRequest profileUpdateRequest) {
		validateProfileUpdatePermission(requestMemberId, targetMemberId);
		MemberImpl targetMember = findTargetMember(targetMemberId);
		String name = profileUpdateRequest.getName();
		String introduction = profileUpdateRequest.getIntroduction();
		targetMember.updateProfile(name, introduction);
		return makeProfileResponse(targetMemberId);
	}

	private void validateProfileUpdatePermission(Long requestMemberId, Long targetMemberId) {
		if (!isOwnerOfProfile(requestMemberId, targetMemberId)) {
			throw new CafegoryException(PROFILE_UPDATE_PERMISSION_DENIED);
		}
	}

	private MemberImpl findTargetMember(Long targetMemberId) {
		Optional<MemberImpl> targetMember = memberRepository.findById(targetMemberId);
		if (targetMember.isEmpty()) {
			throw new CafegoryException(MEMBER_NOT_FOUND);
		}
		return targetMember.get();
	}

	private ProfileResponse makeProfileResponse(Long targetMemberID) {
		MemberImpl member = memberRepository.findById(targetMemberID).orElseThrow();
		return new ProfileResponse(member.getName(), member.getThumbnailImage().getThumbnailImage(),
			member.getIntroduction());
	}

	private boolean isOwnerOfProfile(Long requestMemberID, Long targetMemberID) {
		if (requestMemberID == null || targetMemberID == null) {
			return false;
		}
		return requestMemberID.equals(targetMemberID);
	}

	private boolean isAllowedCauseStudyLeader(Long requestMemberID, Long targetMemberID) {
		List<StudyOnceImpl> studyOnceByLeaderID = findStudyOnceByLeaderID(requestMemberID);
		Set<Long> memberIdInStudyOnce = getMemberIdInStudyOnce(studyOnceByLeaderID);
		return memberIdInStudyOnce.contains(targetMemberID);
	}

	private List<StudyOnceImpl> findStudyOnceByLeaderID(Long requestMemberID) {
		return studyOnceRepository.findByLeaderId(requestMemberID);
	}

	private Set<Long> getMemberIdInStudyOnce(List<StudyOnceImpl> byLeaderId) {
		return mapToMemberId(byLeaderId.stream()
			.flatMap(studyOnce -> studyOnce.getStudyMembers().stream())
			.collect(Collectors.toList()));
	}

	private static Set<Long> mapToMemberId(List<StudyMember> studyMembers) {
		return studyMembers.stream()
			.map(StudyMember::getMember)
			.map(MemberImpl::getId)
			.collect(Collectors.toSet());
	}

	private boolean isAllowedCauseSameStudyOnceMember(Long requestMemberID, Long targetMemberID, LocalDateTime base) {
		MemberImpl requestMember = memberRepository.findById(requestMemberID).orElseThrow();
		List<StudyMember> studyMembers = findAllSameStudyOnceStudyMembersWith(requestMember, base);
		Set<Long> memberIdsThatJoinWithRequestMember = mapToMemberId(studyMembers);
		return memberIdsThatJoinWithRequestMember.contains(targetMemberID);
	}

	private List<StudyMember> findAllSameStudyOnceStudyMembersWith(MemberImpl requestMember, LocalDateTime base) {
		LocalDate baseDate = LocalDate.from(base);
		return studyMemberRepository.findByMemberAndStudyDate(requestMember, baseDate)
			.stream()
			.map(StudyMember::getStudy)
			.filter(studyOnce -> !studyOnce.canJoin(base))
			.filter(studyOnce -> !studyOnce.isEnd())
			.flatMap(studyOnce -> studyOnce.getStudyMembers().stream())
			.collect(Collectors.toList());
	}
}
