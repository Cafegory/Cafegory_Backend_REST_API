package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.domain.study.StudyMember;
import com.example.demo.dto.member.MemberResponse;

public class StudyMemberMapper {

	public List<MemberResponse> toMemberResponses(List<StudyMember> studyMembers) {
		return studyMembers.stream()
			.map(studyMember -> new MemberResponse(
				studyMember.getMember().getId(),
				studyMember.getMember().getName(),
				studyMember.getMember().getThumbnailImage().getThumbnailImage()))
			.collect(Collectors.toList());
	}

}
