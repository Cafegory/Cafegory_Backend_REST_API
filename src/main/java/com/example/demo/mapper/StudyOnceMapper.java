package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.domain.StudyOnceComment;
import com.example.demo.domain.StudyOnceImpl;
import com.example.demo.domain.cafe.CafeImpl;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.dto.StudyOnceCommentResponse;
import com.example.demo.dto.StudyOnceCreateRequest;
import com.example.demo.dto.StudyOnceForCafeResponse;
import com.example.demo.dto.StudyOnceSearchResponse;
import com.example.demo.dto.WriterResponse;

public class StudyOnceMapper {

	public List<StudyOnceForCafeResponse> toStudyOnceForCafeResponse(CafeImpl findCafe) {
		return findCafe.getStudyOnceGroup().stream()
			.map(studyOnce ->
				StudyOnceForCafeResponse.builder()
					.cafeId(findCafe.getId())
					.studyOnceId(studyOnce.getId())
					.name(studyOnce.getName())
					.startDateTime(studyOnce.getStartDateTime())
					.endDateTime(studyOnce.getEndDateTime())
					.maxMemberCount(studyOnce.getMaxMemberCount())
					.nowMemberCount(studyOnce.getNowMemberCount())
					.isEnd(studyOnce.isAbleToTalk())
					.build()
			)
			.collect(Collectors.toList());
	}

	public StudyOnceImpl toNewEntity(StudyOnceCreateRequest studyOnceCreateRequest, CafeImpl cafe,
		MemberImpl leader) {
		return StudyOnceImpl.builder()
			.name(studyOnceCreateRequest.getName())
			.startDateTime(studyOnceCreateRequest.getStartDateTime())
			.endDateTime(studyOnceCreateRequest.getEndDateTime())
			.maxMemberCount(studyOnceCreateRequest.getMaxMemberCount())
			.nowMemberCount(0)
			.isEnd(false)
			.ableToTalk(studyOnceCreateRequest.isCanTalk())
			.cafe(cafe)
			.leader(leader)
			.build();
	}

	public StudyOnceSearchResponse toStudyOnceSearchResponse(StudyOnceImpl saved, boolean canJoin) {
		return StudyOnceSearchResponse.builder()
			.cafeId(saved.getCafe().getId())
			.area(saved.getCafe().getRegion())
			.studyOnceId(saved.getId())
			.name(saved.getName())
			.startDateTime(saved.getStartDateTime())
			.endDateTime(saved.getEndDateTime())
			.maxMemberCount(saved.getMaxMemberCount())
			.nowMemberCount(saved.getNowMemberCount())
			.canTalk(saved.isAbleToTalk())
			.canJoin(canJoin)
			.isEnd(saved.isEnd())
			.build();
	}

	public StudyOnceCommentResponse toStudyOnceQuestionResponse(StudyOnceComment question,
		WriterResponse writerResponse) {
		return new StudyOnceCommentResponse(question.getId(), question.getContent(), writerResponse);
	}

}
